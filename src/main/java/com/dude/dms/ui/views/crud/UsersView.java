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
    public UsersView(UserService userService) {
        super(User.class, userService);
    }

    @Override
    protected void defineProperties() {
        ComboBox<String> roles = new ComboBox<>();
        roles.setItems(Role.getAllRoles());
        roles.setPreventInvalidInput(true);
        roles.setAllowCustomValue(false);
        addProperty("Login", new TextField(), User::getLogin, User::setLogin, s -> !s.isEmpty(), "Login can not be empty!");
        addProperty("Role", roles, User::getRole, User::setRole, s -> s != null && !s.isEmpty(), "Role can not be empty!");
    }
}