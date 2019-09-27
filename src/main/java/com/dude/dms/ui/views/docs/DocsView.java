package com.dude.dms.ui.views.docs;

import com.dude.dms.backend.data.entity.Doc;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.utils.Const;
import com.dude.dms.ui.views.HistoricalCrudView;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

@Tag("docs-view")
@JsModule("./src/views/docs/docs-view.js")
@Route(value = Const.PAGE_DOCS, layout = MainView.class)
@RouteAlias(value = Const.PAGE_ROOT, layout = MainView.class)
@PageTitle(Const.TITLE_DOCS)
public class DocsView extends HistoricalCrudView implements AfterNavigationObserver {

    @Autowired
    private DocService docService;

    @Id("grid")
    private Grid<Doc> grid;
    @Id("title")
    private TextField title;
    @Id("guid")
    private TextField guid;
    @Id("cancel")
    private Button cancel;
    @Id("save")
    private Button save;

    private final Binder<Doc> binder;

    public DocsView() {
        grid.addColumn(Doc::getTitle).setHeader("Title");
        grid.addColumn(Doc::getGuid).setHeader("GUID");

        grid.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        binder = new Binder<>(Doc.class);
        binder.bindInstanceFields(this);
        cancel.addClickListener(e -> grid.asSingleSelect().clear());
        save.addClickListener(e -> Notification.show("Not implemented"));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(docService.findAll());
    }

    private void populateForm(Doc value) {
        binder.readBean(value);
    }
}