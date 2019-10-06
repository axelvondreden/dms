package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.brain.BrainUtils;
import com.dude.dms.backend.brain.OptionKey;
import com.dude.dms.backend.data.base.Doc;
import com.dude.dms.backend.data.history.DocHistory;
import com.dude.dms.backend.service.DocHistoryService;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.components.standard.DmsDatePicker;
import com.dude.dms.ui.components.tags.TagContainer;
import com.dude.dms.ui.converters.LocalDateConverter;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.nio.file.Paths;

@Route(value = Const.PAGE_DOCS, layout = MainView.class)
@RouteAlias(value = Const.PAGE_ROOT, layout = MainView.class)
@PageTitle(Const.TITLE_DOCS)
public class DocsView extends HistoricalCrudView<Doc, DocHistory> {

    @Autowired
    private TagService tagService;

    @Autowired
    public DocsView(DocService docService, DocHistoryService historyService) {
        super(Doc.class, docService, historyService);
    }

    @Override
    protected void defineProperties() {
        addProperty("GUID", new TextField(), Doc::getGuid, Doc::setGuid, true);
        addProperty("Date", new DmsDatePicker(), Doc::getDocumentDate, doc -> LocalDateConverter.convert(doc.getDocumentDate()), Doc::setDocumentDate);
        addGridColumn("Raw Text", doc -> {
            Path path = Paths.get(BrainUtils.getProperty(OptionKey.DOC_SAVE_PATH), doc.getGuid() + ".pdf").toAbsolutePath();
            return new HorizontalLayout(
                    new Button(VaadinIcon.TEXT_LABEL.create(), e -> openTextDialog(doc)),
                    new Button(VaadinIcon.FILE_TEXT.create(), e -> UI.getCurrent().getPage().executeJs("window.open('file:" + path + "', '_blank');")));
        });
        addGridColumn("Tags", doc -> new TagContainer(tagService.findByDoc(doc)));
        showCreateButton(false);
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
}