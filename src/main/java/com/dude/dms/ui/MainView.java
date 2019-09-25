package com.dude.dms.ui;

import com.dude.dms.app.security.SecurityUtils;
import com.dude.dms.ui.utils.Const;
import com.dude.dms.ui.views.HasConfirmation;
import com.dude.dms.ui.views.accounts.AccountsView;
import com.dude.dms.ui.views.docs.DocsView;
import com.dude.dms.ui.views.persons.PersonsView;
import com.dude.dms.ui.views.tags.TagsView;
import com.dude.dms.ui.views.users.UsersView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.Orientation;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Theme(value = Lumo.class, variant = Lumo.DARK)
@Viewport(Const.VIEWPORT)
@PWA(name = "dms", shortName = "dms", startPath = "login", backgroundColor = "#227aef", themeColor = "#227aef", offlinePath = "offline-page.html", offlineResources = "images/offline-login-banner.jpg")
public class MainView extends AppLayout {

    private final ConfirmDialog confirmDialog = new ConfirmDialog();
    private final Tabs menu;

    public MainView() {
        confirmDialog.setCancelable(true);
        confirmDialog.setConfirmButtonTheme("raised tertiary error");
        confirmDialog.setCancelButtonTheme("raised tertiary");

        setDrawerOpened(false);
        Span appName = new Span("dms");
        appName.addClassName("hide-on-mobile");

        menu = createMenuTabs();

        addToNavbar(appName);
        addToNavbar(true, menu);
        getElement().appendChild(confirmDialog.getElement());

        getElement().addEventListener("search-focus", e -> getElement().getClassList().add("hide-navbar"));

        getElement().addEventListener("search-blur", e -> getElement().getClassList().remove("hide-navbar"));
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        confirmDialog.setOpened(false);
        if (getContent() instanceof HasConfirmation) {
            ((HasConfirmation) getContent()).setConfirmDialog(confirmDialog);
        }

        String target = null;
        try {
            target = RouteConfiguration.forSessionScope().getUrl(getContent().getClass());
        } catch (IllegalArgumentException | NotFoundException e) {
            //TODO
        }
        if (target != null) {
            String finalTarget = target;
            Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
                Component child = tab.getChildren().findFirst().get();
                return child instanceof RouterLink && ((RouterLink) child).getHref().equals(finalTarget);
            }).findFirst();
            tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
        }
    }

    private static Tabs createMenuTabs() {
        Tabs tabs = new Tabs();
        tabs.setOrientation(Orientation.HORIZONTAL);
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private static Tab[] getAvailableTabs() {
        List<Tab> tabs = new ArrayList<>(4);
        tabs.add(createTab(VaadinIcon.EDIT, Const.TITLE_DOCS, DocsView.class));
        if (SecurityUtils.isAccessGranted(PersonsView.class)) {
            tabs.add(createTab(VaadinIcon.USERS, Const.TITLE_PERSONS, PersonsView.class));
        }
        if (SecurityUtils.isAccessGranted(AccountsView.class)) {
            tabs.add(createTab(VaadinIcon.ACCESSIBILITY, Const.TITLE_ACCOUNTS, AccountsView.class));
        }
        if (SecurityUtils.isAccessGranted(UsersView.class)) {
            tabs.add(createTab(VaadinIcon.USER, Const.TITLE_USERS, UsersView.class));
        }
        if (SecurityUtils.isAccessGranted(TagsView.class)) {
            tabs.add(createTab(VaadinIcon.CALENDAR, Const.TITLE_TAGS, TagsView.class));
        }
        String contextPath = VaadinServlet.getCurrent().getServletContext().getContextPath();
        Tab logoutTab = createTab(createLogoutLink(contextPath));
        tabs.add(logoutTab);
        return tabs.toArray(new Tab[tabs.size()]);
    }

    private static Tab createTab(VaadinIcon icon, String title, Class<? extends Component> viewClass) {
        return createTab(populateLink(new RouterLink(null, viewClass), icon, title));
    }

    private static Tab createTab(Component content) {
        Tab tab = new Tab();
        tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        tab.add(content);
        return tab;
    }

    private static Anchor createLogoutLink(String contextPath) {
        Anchor a = populateLink(new Anchor(), VaadinIcon.ARROW_RIGHT, Const.TITLE_LOGOUT);
        a.setHref(contextPath + "/logout");
        return a;
    }

    private static <T extends HasComponents> T populateLink(T a, VaadinIcon icon, String title) {
        a.add(icon.create());
        a.add(title);
        return a;
    }
}
