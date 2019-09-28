package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.data.Role;
import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.data.entity.UserHistory;
import com.dude.dms.backend.service.UserService;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.utils.Const;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = Const.PAGE_USERS, layout = MainView.class)
@PageTitle(Const.TITLE_USERS)
public class UsersView extends HistoricalCrudView<User, UserHistory> {

    @Autowired
    private UserService userService;

    public UsersView() {

    }

    protected void setColumns() {
        grid.addColumn(User::getLogin).setHeader("Login");
        grid.addColumn(User::getRole).setHeader("Role");
    }

    protected void fillGrid() {
        grid.setItems(userService.findAll());
    }

    @Override
    protected void attachBinder() {
        ComboBox<String> roles = new ComboBox<>();
        roles.setItems(Role.getAllRoles());
        roles.setPreventInvalidInput(true);
        roles.setAllowCustomValue(false);
        crudForm.addFormField("Login", new TextField(), User::getLogin, User::setLogin);
        crudForm.addFormField("Role", roles, User::getRole, User::setRole);
    }
}