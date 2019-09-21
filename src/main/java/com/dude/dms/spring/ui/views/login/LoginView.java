package com.dude.dms.spring.ui.views.login;

import com.dude.dms.spring.app.security.SecurityUtils;
import com.dude.dms.spring.ui.Const;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginI18n.Form;
import com.vaadin.flow.component.login.LoginI18n.Header;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.*;

@Route
@PageTitle("dms")
@Viewport(Const.VIEWPORT)
public class LoginView extends VerticalLayout implements AfterNavigationObserver, BeforeEnterObserver {

  private final LoginOverlay login = new LoginOverlay();

  public LoginView() {
    LoginI18n i18n = LoginI18n.createDefault();
    i18n.setHeader(new Header());
    i18n.getHeader().setTitle("dms");
    i18n.setAdditionalInformation(null);
    i18n.setForm(new Form());
    i18n.getForm().setSubmit(getTranslation("Login"));
    i18n.getForm().setTitle(getTranslation("Login"));
    i18n.getForm().setUsername(getTranslation("Benutzer"));
    i18n.getForm().setPassword(getTranslation("Passwort"));
    login.setI18n(i18n);
    login.getElement().setAttribute("no-forgot-password", true);
    login.setAction("login");
    login.setOpened(true);
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    if (SecurityUtils.isUserLoggedIn()) {
      // Needed manually to change the URL because of https://github.com/vaadin/flow/issues/4189
      UI.getCurrent().getPage().getHistory().replaceState(null, "");
      //event.rerouteTo(TrackingView.class);
    }
  }

  @Override
  public void afterNavigation(AfterNavigationEvent event) {
    login.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
  }

}