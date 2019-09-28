package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.data.entity.Doc;
import com.dude.dms.backend.data.entity.DocHistory;
import com.dude.dms.backend.service.DocHistoryService;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.utils.Const;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = Const.PAGE_DOCS, layout = MainView.class)
@RouteAlias(value = Const.PAGE_ROOT, layout = MainView.class)
@PageTitle(Const.TITLE_DOCS)
public class DocsView extends HistoricalCrudView<Doc, DocHistory> {

    @Autowired
    public DocsView(DocService docService, DocHistoryService historyService) {
        super(Doc.class, docService, historyService);
    }

    @Override
    protected void defineProperties() {
        addProperty("Title", new TextField(), Doc::getTitle, Doc::setTitle, s -> !s.isEmpty(), "Title can not be empty!");
        addProperty("GUID", new TextField(), Doc::getGuid, Doc::setGuid);
    }
}