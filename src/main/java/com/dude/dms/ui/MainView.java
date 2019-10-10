package com.dude.dms.ui;

import com.dude.dms.backend.data.base.Tag;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.components.crud.TagCreateDialog;
import com.dude.dms.ui.components.crud.TagEditDialog;
import com.dude.dms.ui.components.search.DmsSearchOverlayButton;
import com.dude.dms.ui.components.search.DmsSearchOverlayButtonBuilder;
import com.dude.dms.ui.components.search.DocSearchResult;
import com.dude.dms.ui.components.search.SearchResult;
import com.dude.dms.ui.views.OptionsView;
import com.dude.dms.ui.views.RulesView;
import com.dude.dms.ui.views.crud.DocsView;
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
import com.github.appreciated.card.label.SecondaryLabel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.data.provider.DataProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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

            ContextMenu contextMenu = new ContextMenu();
            contextMenu.setTarget(entry);
            contextMenu.addItem("Edit", e -> new TagEditDialog(tagService).open(tag));
        }

        return new LeftSubmenu("Tags", VaadinIcon.TAGS.create(), tagEntries);
    }

    private FlexLayout buildAppBar() {
        DmsSearchOverlayButton<SearchResult> searchOverlayButton = initSearchOverlayButton();
        return AppBarBuilder.get().add(searchOverlayButton).build();
    }

    private DmsSearchOverlayButton<SearchResult> initSearchOverlayButton() {
        return new DmsSearchOverlayButtonBuilder<SearchResult>()
                .withDataProvider(createDataProvider())
                .withDataViewProvider(result -> {
                    RippleClickableCard card = new RippleClickableCard(new SecondaryLabel(result.getHeader()), result.getBody());
                    card.setWidthFull();
                    card.setBackground("var(--lumo-base-color)");
                    return card;
                })
                .withQueryResultListener(searchResult -> searchResult.onClick())
                .build();
    }

    DataProvider<SearchResult, String> createDataProvider() {
        return DataProvider.fromFilteringCallbacks(query -> {
            if (query.getFilter().isPresent()) {
                String filter = query.getFilter().get();
                return docService.findTop10ByRawTextLike("%" + filter + "%").stream().map(doc -> new DocSearchResult(doc, filter));
            }
            return null;
        }, query -> {
            if (query.getFilter().isPresent()) {
                return (int) docService.countByRawTextLike("%" + query.getFilter().get() + "%");
            }
            return 0;
        });
    }
}