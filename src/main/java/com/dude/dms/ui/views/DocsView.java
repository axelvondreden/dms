package com.dude.dms.ui.views;

import com.dude.dms.backend.brain.parsing.PdfToDocParser;
import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.history.DocHistory;
import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.tags.TagContainer;
import com.dude.dms.ui.converters.LocalDateConverter;
import com.helger.commons.io.file.FileHelper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

    private final BuilderFactory builderFactory;

    private final DocService docService;
    private final TagService tagService;

    private String param;

    @Autowired
    public DocsView(BuilderFactory builderFactory, DocService docService, TagService tagService, PdfToDocParser pdfToDocParser) {
        this.builderFactory = builderFactory;
        this.docService = docService;
        this.tagService = tagService;
        UI ui = UI.getCurrent();
        pdfToDocParser.addEventListener("docs", success -> {
            if (success) {
                ui.access(this::fillGrid);
            }
        });
        addColumn("Date", doc -> LocalDateConverter.convert(doc.getDocumentDate()));
        addComponentColumn("Tags", doc -> new TagContainer(doc.getTags()));
        addComponentColumn("", this::createGridActions);
        addColumn("GUID", Doc::getGuid);

        grid.addItemDoubleClickListener(event -> builderFactory.docs().imageDialog(event.getItem()).build().open());
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

        return new HorizontalLayout(
                new Button(VaadinIcon.TEXT_LABEL.create(), e -> builderFactory.docs().textDialog(doc).build().open()),
                download,
                new Button(VaadinIcon.EDIT.create(), e -> builderFactory.docs().editDialog(doc).withEventListener(() -> grid.getDataProvider().refreshAll()).build().open())
        );
    }

    private void fillGrid() {
        if (param != null && !param.isEmpty()) {
            String[] parts = param.split(":");
            if ("tag".equalsIgnoreCase(parts[0])) {
                Optional<Tag> tag = tagService.findByName(parts[1]);
                tag.ifPresent(t -> grid.setItems(docService.findByTag(t)));
            }
        } else {
            grid.setItems(docService.findAll());
        }
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String t) {
        param = t;
        fillGrid();
    }
}