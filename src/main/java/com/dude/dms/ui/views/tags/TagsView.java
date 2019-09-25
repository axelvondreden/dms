package com.dude.dms.ui.views.tags;

import com.dude.dms.backend.data.entity.Tag;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.utils.Const;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;

@com.vaadin.flow.component.Tag("tags-view")
@JsModule("./src/views/tags/tags-view.js")
@Route(value = Const.PAGE_TAGS, layout = MainView.class)
@PageTitle(Const.TITLE_TAGS)
public class TagsView extends PolymerTemplate<TemplateModel> implements AfterNavigationObserver {

    @Autowired
    private TagService tagService;

    @Id("grid")
    private Grid<Tag> grid;
    @Id("name")
    private TextField name;
    @Id("cancel")
    private Button cancel;
    @Id("save")
    private Button save;

    private final Binder<Tag> binder;

    public TagsView() {
        grid.addColumn(Tag::getName).setHeader("Name");

        grid.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        binder = new Binder<>(Tag.class);
        binder.bindInstanceFields(this);
        cancel.addClickListener(e -> grid.asSingleSelect().clear());
        save.addClickListener(e -> Notification.show("Not implemented"));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(tagService.findAll());
    }

    private void populateForm(Tag value) {
        binder.readBean(value);
    }
}