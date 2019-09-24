package com.dude.dms.ui.views.admin.tags;

import com.dude.dms.app.security.CurrentUser;
import com.dude.dms.backend.data.Role;
import com.dude.dms.backend.data.entity.Tag;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.crud.AbstractCrudView;
import com.dude.dms.ui.utils.Const;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

@Route(value = Const.PAGE_TAGS, layout = MainView.class)
@PageTitle(Const.TITLE_TAGS)
@Secured(Role.ADMIN)
public class TagsView extends AbstractCrudView<Tag> {

    @Autowired
    public TagsView(TagService service, CurrentUser currentUser) {
        super(Tag.class, service, new Grid<>(), createForm(), currentUser);
    }

    @Override
    protected void setupGrid(Grid<Tag> grid) {
        grid.addColumn(Tag::getName).setHeader("Name").setFlexGrow(10);
    }

    @Override
    protected String getBasePage() {
        return Const.PAGE_TAGS;
    }

    private static BinderCrudEditor<Tag> createForm() {
        TextField name = new TextField("Name");
        name.getElement().setAttribute("colspan", "2");

        FormLayout form = new FormLayout(name);

        BeanValidationBinder<Tag> binder = new BeanValidationBinder<>(Tag.class);
        binder.bind(name, "name");
        return new BinderCrudEditor<>(binder, form);
    }

}
