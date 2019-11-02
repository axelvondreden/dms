package com.dude.dms.ui;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.brain.parsing.PdfToDocParser;
import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.search.DmsSearchOverlayButton;
import com.dude.dms.ui.views.DocsView;
import com.dude.dms.ui.views.LogView;
import com.dude.dms.ui.views.OptionsView;
import com.dude.dms.ui.views.RulesView;
import com.github.appreciated.app.layout.component.appbar.AppBarBuilder;
import com.github.appreciated.app.layout.component.applayout.LeftLayouts;
import com.github.appreciated.app.layout.component.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.menu.left.LeftSubmenu;
import com.github.appreciated.app.layout.component.menu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.component.menu.left.items.LeftClickableItem;
import com.github.appreciated.app.layout.component.menu.left.items.LeftNavigationItem;
import com.github.appreciated.app.layout.component.router.AppLayoutRouterLayout;
import com.github.appreciated.app.layout.entity.DefaultBadgeHolder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

import static com.dude.dms.backend.brain.OptionKey.DARK_MODE;
import static com.github.appreciated.app.layout.entity.Section.FOOTER;
import static com.github.appreciated.app.layout.entity.Section.HEADER;

@Push
public class MainView extends AppLayoutRouterLayout<LeftLayouts.LeftHybrid> implements AfterNavigationObserver {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(MainView.class);

    private final DocService docService;

    private final TagService tagService;

    private final BuilderFactory builderFactory;

    private final String buildVersion;

    private DefaultBadgeHolder docsBadge;

    @Autowired
    public MainView(DocService docService, TagService tagService, PdfToDocParser pdfToDocParser, BuilderFactory builderFactory, @Value("${build.version}") String buildVersion) {
        this.docService = docService;
        this.tagService = tagService;
        this.builderFactory = builderFactory;
        this.buildVersion = buildVersion;

        init(AppLayoutBuilder.get(LeftLayouts.LeftHybrid.class)
                .withTitle("dms")
                .withAppBar(buildAppBar())
                .withAppMenu(buildAppMenu())
                .build());

        UI ui = UI.getCurrent();
        pdfToDocParser.addEventListener("main", success -> {
            if (success) {
                ui.access(() -> {
                    docsBadge.increase();
                    LOGGER.showInfo("New doc added!");
                });
            }
        });
    }

    private Component buildAppMenu() {
        docsBadge = new DefaultBadgeHolder((int) docService.count());
        LeftNavigationItem docsEntry = new LeftNavigationItem("Docs", VaadinIcon.FILE_TEXT.create(), DocsView.class);
        docsBadge.bind(docsEntry.getBadge());

        LeftSubmenu tagsEntry = createTagsEntry();
        LeftNavigationItem rulesEntry = new LeftNavigationItem("Rules", VaadinIcon.MAGIC.create(), RulesView.class);
        LeftNavigationItem logEntry = new LeftNavigationItem("Log", VaadinIcon. CLIPBOARD_PULSE.create(), LogView.class);
        return LeftAppMenuBuilder.get()
                .addToSection(HEADER, new LeftClickableItem("Add doc", VaadinIcon.PLUS_CIRCLE.create(), e -> builderFactory.docs().createDialog().build().open()))
                .add(docsEntry, tagsEntry, rulesEntry, logEntry)
                .withStickyFooter()
                .addToSection(FOOTER,
                        new LeftClickableItem(buildVersion, VaadinIcon.HAMMER.create(), e -> builderFactory.misc().changelog().build().open()),
                        new LeftNavigationItem("Settings", VaadinIcon.COG.create(), OptionsView.class))
                .build();
    }

    private LeftSubmenu createTagsEntry() {
        List<Component> tagEntries = new ArrayList<>();
        tagEntries.add(new LeftClickableItem("Add Tag", VaadinIcon.PLUS_CIRCLE.create(), event -> builderFactory.tags().createDialog().withEventListener(() -> UI.getCurrent().getPage().reload()).build().open()));
        for (Tag tag : tagService.findAll()) {
            DefaultBadgeHolder badgeHolder = new DefaultBadgeHolder((int) docService.countByTag(tag));
            Icon icon = VaadinIcon.TAG.create();
            icon.setColor(tag.getColor());
            LeftClickableItem entry = new LeftClickableItem(tag.getName(), icon, clickEvent -> UI.getCurrent().navigate(DocsView.class, "tag:" + tag.getName()));
            tagEntries.add(entry);
            badgeHolder.bind(entry.getBadge());

            ContextMenu contextMenu = new ContextMenu();
            contextMenu.setTarget(entry);
            contextMenu.addItem("Edit", e -> builderFactory.tags().editDialog(tag).withEventListener(() -> UI.getCurrent().getPage().reload()).build().open());
        }
        return new LeftSubmenu("Tags", VaadinIcon.TAGS.create(), tagEntries);
    }

    private FlexLayout buildAppBar() {
        DmsSearchOverlayButton searchOverlayButton = initSearchOverlayButton();
        return AppBarBuilder.get().add(searchOverlayButton).build();
    }

    private DmsSearchOverlayButton initSearchOverlayButton() {
        DmsSearchOverlayButton button = new DmsSearchOverlayButton(builderFactory);
        button.getSearchView().initDataproviders(docService, tagService);
        return button;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        themeList.clear();
        themeList.add(DARK_MODE.getBoolean() ? Lumo.DARK : Lumo.LIGHT);
    }
}