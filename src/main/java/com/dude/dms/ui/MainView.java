package com.dude.dms.ui;

import com.dude.dms.ui.views.crud.AccountsView;
import com.dude.dms.ui.views.crud.DocsView;
import com.dude.dms.ui.views.crud.TagsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.Orientation;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Theme(value = Lumo.class, variant = Lumo.DARK)
@Viewport(Const.VIEWPORT)
public class MainView extends AppLayout {

    private final Tabs menu;

    public MainView() {
        setDrawerOpened(false);

        menu = createMenuTabs();

        addToNavbar(true, menu);
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();

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
        List<Tab> tabs = new ArrayList<>();
        tabs.add(createTab(VaadinIcon.EDIT, Const.TITLE_DOCS, DocsView.class));
        tabs.add(createTab(VaadinIcon.ACCESSIBILITY, Const.TITLE_ACCOUNTS, AccountsView.class));
        tabs.add(createTab(VaadinIcon.CALENDAR, Const.TITLE_TAGS, TagsView.class));
        return tabs.toArray(new Tab[0]);
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

    private static <T extends HasComponents> T populateLink(T a, VaadinIcon icon, String title) {
        a.add(icon.create());
        a.add(title);
        return a;
    }
}
