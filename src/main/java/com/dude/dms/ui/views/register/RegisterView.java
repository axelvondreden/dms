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
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route("register")
@Tag("register-view")
@JsModule("./src/views/register/register-view.js")
public class RegisterView extends PolymerTemplate<TemplateModel> implements HasNotifications {

    @Id("userName")
    private TextField userName;
    @Id("firstName")
    private TextField firstName;
    @Id("lastName")
    private TextField lastName;
    @Id("password1")
    private PasswordField password1;
    @Id("password2")
    private PasswordField password2;
    @Id("roles")
    private ComboBox<String> roles;
    @Id("submit")
    private Button submit;

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
