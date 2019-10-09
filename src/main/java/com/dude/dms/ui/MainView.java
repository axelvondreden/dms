package com.dude.dms.ui;

import com.dude.dms.backend.data.base.Tag;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.components.crud.TagCreateDialog;
import com.dude.dms.ui.components.search.DocSearchResult;
import com.dude.dms.ui.components.search.SearchResult;
import com.dude.dms.ui.views.OptionsView;
import com.dude.dms.ui.views.RulesView;
import com.dude.dms.ui.views.crud.DocsView;
import com.github.appreciated.app.layout.addons.search.SearchButton;
import com.github.appreciated.app.layout.addons.search.overlay.SearchOverlayButton;
import com.github.appreciated.app.layout.addons.search.overlay.SearchOverlayButtonBuilder;
import com.github.appreciated.app.layout.component.appbar.AppBarBuilder;
import com.github.appreciated.app.layout.component.applayout.LeftLayouts;
import com.github.appreciated.app.layout.component.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.menu.left.LeftSubmenu;
import com.github.appreciated.app.layout.component.menu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.component.menu.left.items.LeftClickableItem;
import com.github.appreciated.app.layout.component.menu.left.items.LeftHeaderItem;
import com.github.appreciated.app.layout.component.menu.left.items.LeftNavigationItem;
import com.github.appreciated.app.layout.component.router.AppLayoutRouterLayout;
import com.github.appreciated.app.layout.entity.DefaultBadgeHolder;
import com.github.appreciated.card.RippleClickableCard;
import com.github.appreciated.card.content.Item;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.function.SerializablePredicate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.appreciated.app.layout.entity.Section.FOOTER;
import static com.github.appreciated.app.layout.entity.Section.HEADER;

@Push
public class MainView extends AppLayoutRouterLayout<LeftLayouts.LeftResponsiveHybrid> {

    private final DocService docService;

    private final TagService tagService;

    @Autowired
    public MainView(DocService docService, TagService tagService) {
        this.docService = docService;
        this.tagService = tagService;

        init(AppLayoutBuilder.get(LeftLayouts.LeftResponsiveHybrid.class)
                .withTitle("dms")
                .withAppBar(buildAppBar())
                .withAppMenu(buildAppMenu())
                .build());
    }

    private Component buildAppMenu() {
        DefaultBadgeHolder docsBadge = new DefaultBadgeHolder((int) docService.count());
        LeftNavigationItem docsEntry = new LeftNavigationItem("Docs", VaadinIcon.FILE_TEXT.create(), DocsView.class);
        docsBadge.bind(docsEntry.getBadge());

        LeftSubmenu tagsEntry = createTagsEntry();
        LeftClickableItem newTagEntry = new LeftClickableItem("Create Tag", VaadinIcon.TAG.create(), event -> new TagCreateDialog(tagService).open());
        LeftNavigationItem rulesEntry = new LeftNavigationItem("Rules", VaadinIcon.FORM.create(), RulesView.class);

        return LeftAppMenuBuilder.get()
                .addToSection(HEADER, new LeftHeaderItem("Header Text", "Subtitle", null))
                .add(docsEntry, tagsEntry, newTagEntry, rulesEntry)
                .addToSection(FOOTER, new LeftNavigationItem("Settings", VaadinIcon.COG.create(), OptionsView.class))
                .build();
    }

    private LeftSubmenu createTagsEntry() {
        List<Component> tagEntries = new ArrayList<>();
        for (Tag tag: tagService.findAll()) {
            DefaultBadgeHolder badgeHolder = new DefaultBadgeHolder((int) docService.countByTag(tag));
            Icon icon = VaadinIcon.TAG.create();
            icon.setColor(tag.getColor());
            LeftClickableItem entry = new LeftClickableItem(tag.getName(), icon, clickEvent -> UI.getCurrent().navigate(DocsView.class, "tag:" + tag.getName()));
            tagEntries.add(entry);
            badgeHolder.bind(entry.getBadge());
        }

        return new LeftSubmenu("Tags", VaadinIcon.TAGS.create(), tagEntries);
    }

    private FlexLayout buildAppBar() {
        SearchOverlayButton<SearchResult> searchOverlayButton = initSearchOverlayButton();
        SearchButton searchButton = new SearchButton().withValueChangeListener(event -> {
            /* React manually to user inputs */
        });
        FlexLayout flexLayout = AppBarBuilder.get().add(searchOverlayButton).build();
        //flexLayout.getElement().getStyle().set("width", "60vw");
        return flexLayout;
    }

    private SearchOverlayButton<SearchResult> initSearchOverlayButton() {
        List<SearchResult> list = docService.findAll().stream().map(DocSearchResult::new).collect(Collectors.toList());
        ListDataProvider<SearchResult> listDataProvider = new ListDataProvider<>(list);
        return new SearchOverlayButtonBuilder<SearchResult>()
                .withDataProvider(listDataProvider)
                // Set the query that is executed to filter the Entities above
                .withQueryProvider(s -> new Query<>(searchResult -> !s.isEmpty() && searchResult.getSearchtext().contains(s)))
                .withDataViewProvider(queryResult -> {
                    RippleClickableCard card = new RippleClickableCard(new Item(queryResult.getHeader(), queryResult.getDescription()));
                    card.setWidthFull();
                    card.setBackground("var(--lumo-base-color)");
                    return card;
                })
                .withQueryResultListener(testSearchResult -> Notification.show(testSearchResult.getHeader()))
                .build();
    }
}