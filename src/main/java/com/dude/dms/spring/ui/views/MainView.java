package com.dude.dms.spring.ui.views;

import com.dude.dms.spring.ui.Const;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.PWA;


@Viewport(Const.VIEWPORT)
@PWA(name = "Bakery App Starter", shortName = "Mon", startPath = "login", backgroundColor = "#227aef", themeColor = "#227aef", offlinePath = "offline-page.html", offlineResources = "images/offline-login-banner.jpg")
public class MainView extends VerticalLayout implements RouterLayout {

  public MainView() {

  }
}
