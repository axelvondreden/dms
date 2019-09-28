package com.dude.dms.ui.views.login;

import com.dude.dms.app.security.SecurityUtils;
import com.dude.dms.ui.utils.Const;
import com.dude.dms.ui.views.crud.DocsView;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginI18n.Form;
import com.vaadin.flow.component.login.LoginI18n.Header;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.*;

@Route
@PageTitle("dms")
@JsModule("./styles/shared-styles.js")
@Viewport(Const.VIEWPORT)
public class LoginView extends LoginOverlay implements AfterNavigationObserver, BeforeEnterObserver {

    public LoginView() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new Header());
        i18n.getHeader().setTitle("dms");
        i18n.setAdditionalInformation(null);
        i18n.setForm(new Form());
        i18n.getForm().setSubmit("Login");
        i18n.getForm().setUsername("Login");
        i18n.getForm().setPassword("Password");
        setI18n(i18n);
        setForgotPasswordButtonVisible(false);
        setAction("login");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (SecurityUtils.isUserLoggedIn()) {
            beforeEnterEvent.forwardTo(DocsView.class);
        } else {
            setOpened(true);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        setError(afterNavigationEvent.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }

}

/*
private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DataGenerator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private User createBaker(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return userRepository.save(
                createUser("baker@vaadin.com", "Heidi", "Carter", passwordEncoder.encode("baker"), Role.BAKER, false));
    }

    private User createBarista(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return userRepository.save(createUser("barista@vaadin.com", "Malin", "Castro",
                passwordEncoder.encode("barista"), Role.BARISTA, true));
    }

    private User createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return userRepository.save(
                createUser("admin@vaadin.com", "Göran", "Rich", passwordEncoder.encode("admin"), Role.ADMIN, true));
    }

    private void createDeletableUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        userRepository.save(
                createUser("peter@vaadin.com", "Peter", "Bush", passwordEncoder.encode("peter"), Role.BARISTA, false));
        userRepository
                .save(createUser("mary@vaadin.com", "Mary", "Ocon", passwordEncoder.encode("mary"), Role.BAKER, true));
    }

    private User createUser(String email, String firstName, String lastName, String passwordHash, String role,
                            boolean locked) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPasswordHash(passwordHash);
        user.setRole(role);
        user.setLocked(locked);
        return user;
    }
 */