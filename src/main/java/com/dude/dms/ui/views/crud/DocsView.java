package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.brain.BrainUtils;
import com.dude.dms.backend.brain.OptionKey;
import com.dude.dms.backend.data.base.Doc;
import com.dude.dms.backend.data.base.Tag;
import com.dude.dms.backend.data.history.DocHistory;
import com.dude.dms.backend.service.DocHistoryService;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.components.crud.DocEditDialog;
import com.dude.dms.ui.components.tags.TagContainer;
import com.dude.dms.ui.converters.LocalDateConverter;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Route(value = Const.PAGE_DOCS, layout = MainView.class)
@RouteAlias(value = Const.PAGE_ROOT, layout = MainView.class)
@PageTitle("Docs")
public class DocsView extends HistoricalCrudView<Doc, DocHistory> implements HasUrlParameter<String> {

    private final DocService docService;
    private final TagService tagService;

    @Autowired
    public DocsView(DocService docService, TagService tagService) {
        super(docService);
        this.docService = docService;
        this.tagService = tagService;

        addColumn("GUID", Doc::getGuid);
        addColumn("Date", doc -> LocalDateConverter.convert(doc.getDocumentDate()));
        addComponentColumn("Tags", doc -> new TagContainer(tagService.findByDoc(doc)));
        addComponentColumn("", doc -> {
            Path path = Paths.get(BrainUtils.getProperty(OptionKey.DOC_SAVE_PATH), doc.getGuid() + ".pdf").toAbsolutePath();
            return new HorizontalLayout(
                    new Button(VaadinIcon.TEXT_LABEL.create(), e -> openTextDialog(doc)),
                    new Button(VaadinIcon.FILE_TEXT.create(), e -> UI.getCurrent().getPage().executeJs("window.open('file:" + path + "', '_blank');")));
        });
        DocEditDialog docEditDialog = new DocEditDialog(docService, tagService);
        addEditDialog(docEditDialog);
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