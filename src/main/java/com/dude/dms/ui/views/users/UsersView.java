package com.dude.dms.ui.views.users;

import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.service.UserService;
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
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Tag("users-view")
@JsModule("./src/views/users/users-view.js")
@Route(value = Const.PAGE_USERS, layout = MainView.class)
@PageTitle(Const.TITLE_USERS)
public class UsersView extends HistoricalCrudView implements AfterNavigationObserver {

    @Autowired
    private UserService userService;

    @Id("grid")
    private Grid<User> grid;
    @Id("login")
    private TextField login;
    @Id("role")
    private TextField role;
    @Id("cancel")
    private Button cancel;
    @Id("save")
    private Button save;

    private final Binder<User> binder;

    public UsersView() {
        grid.addColumn(User::getLogin).setHeader("Login");
        grid.addColumn(User::getRole).setHeader("Role");

        grid.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        binder = new Binder<>(User.class);
        binder.bindInstanceFields(this);
        cancel.addClickListener(e -> grid.asSingleSelect().clear());
        save.addClickListener(e -> Notification.show("Not implemented"));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(userService.findAll());
    }

    private void populateForm(User value) {
        binder.readBean(value);
    }
}