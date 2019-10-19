package com.dude.dms.ui.views;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.tags.Tag;
import com.dude.dms.backend.data.history.DocHistory;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.backend.service.TextBlockService;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.components.dialogs.DocTextDialog;
import com.dude.dms.ui.components.dialogs.crud.DocEditDialog;
import com.dude.dms.ui.components.tags.TagContainer;
import com.dude.dms.ui.converters.LocalDateConverter;
import com.helger.commons.io.file.FileHelper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static com.dude.dms.backend.brain.OptionKey.DOC_SAVE_PATH;

@Route(value = Const.PAGE_DOCS, layout = MainView.class)
@RouteAlias(value = Const.PAGE_ROOT, layout = MainView.class)
@PageTitle("Docs")
public class DocsView extends HistoricalCrudView<Doc, DocHistory> implements HasUrlParameter<String> {

    private final DocService docService;
    private final TagService tagService;
    private final TextBlockService textBlockService;

    @Autowired
    public DocsView(DocService docService, TagService tagService, TextBlockService textBlockService) {
        super(docService);
        this.docService = docService;
        this.tagService = tagService;
        this.textBlockService = textBlockService;
        addColumn("Date", doc -> LocalDateConverter.convert(doc.getDocumentDate()));
        addComponentColumn("Tags", doc -> new TagContainer(doc.getTags()));
        addComponentColumn("", this::createGridActions);
        addColumn("GUID", Doc::getGuid);

        grid.addItemClickListener(event -> UI.getCurrent().navigate(DocView.class, String.valueOf(event.getItem().getId())));
    }

    private HorizontalLayout createGridActions(Doc doc) {
        Path path = Paths.get(DOC_SAVE_PATH.getString(), "pdf", doc.getGuid() + ".pdf").toAbsolutePath();
        File file = path.toFile();
        Anchor download = new Anchor();
        download.add(new Button(VaadinIcon.FILE_TEXT.create()));
        download.setEnabled(false);
        if (file.exists()) {
            download.setEnabled(true);
            download.setHref(new StreamResource("pdf.pdf", () -> FileHelper.getInputStream(file)));
            download.getElement().setAttribute("download", true);
        }

        DocEditDialog docEditDialog = new DocEditDialog(docService, tagService);
        docEditDialog.setEventListener(() -> grid.getDataProvider().refreshAll());

        return new HorizontalLayout(
                new Button(VaadinIcon.TEXT_LABEL.create(), e ->new DocTextDialog().open(textBlockService.findByDoc(doc))),
                download,
                new Button(VaadinIcon.EDIT.create(), e -> docEditDialog.open(doc))
        );
    }

    private static void openTextDialog(Doc doc) {
        TextArea area = new TextArea();
        area.setHeightFull();
        area.setValue(doc.getRawText());
        area.setWidth("80vw");
        area.setReadOnly(true);
        Dialog dialog = new Dialog(area);
        dialog.setSizeFull();
        dialog.open();
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter != null && !parameter.isEmpty()) {
            String[] parts = parameter.split(":");
            if ("tag".equalsIgnoreCase(parts[0])) {
                Optional<Tag> tag = tagService.findByName(parts[1]);
                tag.ifPresent(t -> grid.setItems(docService.findByTag(t)));
            }
        } else {
            grid.setItems(docService.findAll());
        }
    }
}