package com.dude.dms.ui.views.admin.users;

import com.dude.dms.app.security.CurrentUser;
import com.dude.dms.backend.data.Role;
import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.service.UserService;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.crud.AbstractCrudView;
import com.dude.dms.ui.utils.Const;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route(value = Const.PAGE_USERS, layout = MainView.class)
@PageTitle(Const.TITLE_USERS)
@Secured(Role.ADMIN)
public class UsersView extends AbstractCrudView<User> {

    @Autowired
    public UsersView(UserService service, CurrentUser currentUser, PasswordEncoder passwordEncoder) {
        super(User.class, service, new Grid<>(), createForm(passwordEncoder), currentUser);
    }

    @Override
    public void setupGrid(Grid<User> grid) {
        grid.addColumn(User::getLogin).setWidth("270px").setHeader("Login").setFlexGrow(5);
        grid.addColumn(User::getRole).setHeader("Role").setWidth("150px");
    }

    @Override
    protected String getBasePage() {
        return Const.PAGE_USERS;
    }

    private static BinderCrudEditor<User> createForm(PasswordEncoder passwordEncoder) {
        TextField login = new TextField("Login");
        PasswordField password = new PasswordField("Password");
        password.getElement().setAttribute("colspan", "2");
        ComboBox<String> role = new ComboBox<>();
        role.getElement().setAttribute("colspan", "2");
        role.setLabel("Role");

        FormLayout form = new FormLayout(login, password, role);

        BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

        ListDataProvider<String> roleProvider = DataProvider.ofItems(Role.getAllRoles());
        role.setItemLabelGenerator(s -> s != null ? s : "");
        role.setDataProvider(roleProvider);

        binder.bind(login, "login");
        binder.bind(role, "role");

        binder.forField(password)
                .withValidator(pass -> pass != null && pass.length() >= 5, "Password needs to be 5 or more characters!")
                .bind(user -> password.getEmptyValue(), (user, pass) -> {
                    if (!password.getEmptyValue().equals(pass)) {
                        user.setPasswordHash(passwordEncoder.encode(pass));
                    }
                });

        return new BinderCrudEditor<>(binder, form);
    }
}
