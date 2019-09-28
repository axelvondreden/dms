package com.dude.dms.ui.views.register;

import com.dude.dms.backend.data.Role;
import com.dude.dms.backend.data.entity.Person;
import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.service.PersonHistoryService;
import com.dude.dms.backend.service.PersonService;
import com.dude.dms.backend.service.UserHistoryService;
import com.dude.dms.backend.service.UserService;
import com.dude.dms.ui.views.HasNotifications;
import com.dude.dms.ui.views.login.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route("register")
public class RegisterView extends VerticalLayout implements HasNotifications {

    private final TextField userName;
    private final TextField firstName;
    private final TextField lastName;
    private final PasswordField password1;
    private final PasswordField password2;
    private final ComboBox<String> roles;

    @Autowired
    private UserService userService;
    @Autowired
    private UserHistoryService userHistoryService;
    @Autowired
    private PersonService personService;
    @Autowired
    private PersonHistoryService personHistoryService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public RegisterView() {
        setSizeFull();
        getElement().getStyle().set("display", "flex").set("alignItems", "center").set("paddingTop", "100px");

        userName = new TextField();
        userName.addClassName("full-width");
        firstName = new TextField();
        firstName.addClassName("full-width");
        lastName = new TextField();
        lastName.addClassName("full-width");
        password1 = new PasswordField();
        password1.addClassName("full-width");
        password2 = new PasswordField();
        password2.addClassName("full-width");
        roles = new ComboBox<>();
        roles.addClassName("full-width");
        Button submit = new Button();
        submit.addClassName("full-width");
        submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(
                new FormItem(new Label("Username"), userName),
                new FormItem(new Label("First Name"), firstName),
                new FormItem(new Label("Last Name"), lastName),
                new FormItem(new Label("Password"), password1),
                new FormItem(new Label("Password"), password2),
                new FormItem(new Label("Role"), roles),
                new FormItem(submit)
        );

        roles.setItems(Role.getAllRoles());
        submit.addClickListener(e -> {
            if (validate()) {
                register();
            }
        });
    }

    private boolean validate() {
        String errorMsg;
        if (userName.isEmpty()) {
            errorMsg = "Username can not empty!";
        } else if (firstName.isEmpty()) {
            errorMsg = "First name must be filled!";
        } else if (lastName.isEmpty()) {
            errorMsg = "Last name must be filled!";
        } else if (roles.isEmpty()) {
            errorMsg = "Role must be selected!";
        } else if (password1.isEmpty() && password2.isEmpty()) {
            errorMsg = "Password can not be empty!";
        } else if (password1.getValue().length() < 5) {
            errorMsg = "Password needs to be 5 or more characters!";
        } else if (!password1.getOptionalValue().orElse("").equals(password2.getValue())) {
            errorMsg = "Passwords must match!";
        } else if (userService.findByLogin(userName.getValue()).isPresent()) {
            errorMsg = "Login already exists!";
        } else {
            return true;
        }
        showNotification(errorMsg, true);
        return false;
    }

    private void register() {
        User user = userService.create(new User(userName.getValue(), passwordEncoder.encode(password1.getValue()), roles.getValue()));
        Person person = personService.create(new Person(firstName.getValue(), lastName.getValue(), null), user);
        user.setPerson(person);
        userService.save(user);

        UI.getCurrent().navigate(LoginView.class);
    }
}
