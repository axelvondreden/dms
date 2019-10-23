package com.dude.dms.ui.components.search;

import com.dude.dms.backend.data.tags.Tag;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.backend.service.TextBlockService;
import com.github.appreciated.app.layout.addons.search.overlay.QueryPair;
import com.github.appreciated.app.layout.component.appbar.IconButton;
import com.github.appreciated.ironoverlay.IronOverlay;
import com.github.appreciated.ironoverlay.VerticalOrientation;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DmsSearchOverlayView extends IronOverlay {

    private final TextField searchField;

    private final IconButton closeButton;

    private final VerticalLayout results;

    private final VerticalLayout wrapper;

    private final MultiselectComboBox<String> entityMultiselect;

    private final Checkbox caseSensitiveCheckbox;

    private Function<SearchResult, ClickNotifier> dataViewProvider;

    private DataProvider<DocSearchResult, String> docDataProvider;
    private DataProvider<TagSearchResult, String> tagDataProvider;

    private DocService docService;
    private TextBlockService textBlockService;
    private TagService tagService;

    private Consumer<SearchResult> queryResultListener;

    private boolean closeOnQueryResult = true;

    public DmsSearchOverlayView() {
        getElement().getStyle().set("width", "100%");
        setVerticalAlign(VerticalOrientation.TOP);

        results = new VerticalLayout();
        results.setSizeFull();
        results.setMargin(false);
        results.getStyle().set("overflow", "auto");

        searchField = new TextField();
        searchField.getStyle().set("--lumo-contrast-10pct", "transparent");
        searchField.addValueChangeListener(event -> showResults(event.getValue()));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.setWidthFull();

        closeButton = new IconButton(VaadinIcon.ARROW_LEFT.create());
        closeButton.addClickListener(event -> {
            searchField.clear();
            close();
        });

        HorizontalLayout searchFieldWrapper = new HorizontalLayout(closeButton, searchField);
        searchFieldWrapper.getStyle()
                .set("background", "var(--app-layout-bar-background-base-color)")
                .set("height", "var(--app-bar-height)")
                .set("box-shadow", "var(--app-layout-bar-shadow)")
                .set("padding", "var(--app-layout-bar-padding)")
                .set("flex-shrink", "0")
                .set("z-index", "1");
        searchFieldWrapper.setWidthFull();
        searchFieldWrapper.setAlignItems(FlexComponent.Alignment.CENTER);

        entityMultiselect = new MultiselectComboBox<>();
        entityMultiselect.setItems("Docs", "Tags", "Rules");
        entityMultiselect.select("Docs", "Tags", "Rules");
        entityMultiselect.setWidth("30%");

        caseSensitiveCheckbox = new Checkbox("case sensitive");

        HorizontalLayout configWrapper = new HorizontalLayout(new Label("Search in:"), entityMultiselect, caseSensitiveCheckbox);
        configWrapper.setWidthFull();
        configWrapper.setAlignItems(FlexComponent.Alignment.CENTER);
        configWrapper.getStyle()
                .set("background", "var(--app-layout-bar-background-base-color)")
                .set("box-shadow", "var(--app-layout-bar-shadow)")
                .set("padding", "var(--app-layout-bar-padding)")
                .set("flex-shrink", "0")
                .set("z-index", "1");

        wrapper = new VerticalLayout(searchFieldWrapper, configWrapper, results);
        wrapper.setSizeFull();
        wrapper.setAlignItems(FlexComponent.Alignment.CENTER);
        wrapper.setMargin(false);
        wrapper.setPadding(false);
        wrapper.setSpacing(false);
        wrapper.getStyle()
                .set("max-width", "100vw")
                .set("height", "100vh");

        results.getStyle()
                .set("overflow-y", "auto")
                .set("max-width", "100%")
                .set("min-width", "40%")
                .set("--lumo-size-m", "var(--lumo-size-xl)")
                .set("--lumo-contrast-10pct", "transparent");
        results.setHeightFull();
        results.setWidth("unset");
        add(wrapper);
    }

    private void showResults(String value) {
        results.removeAll();
        List<SearchResult> result = docDataProvider.fetch(new Query<>(value)).collect(Collectors.toList());
        result.stream()
                .map(t -> new QueryPair<>(t, dataViewProvider.apply(t)))
                .forEach(clickNotifier -> {
                    results.add((Component) clickNotifier.getNotifier());
                    clickNotifier.getNotifier().addClickListener(clickEvent -> {
                        if (closeOnQueryResult) {
                            close();
                        }
                        if (queryResultListener != null) {
                            queryResultListener.accept(clickNotifier.getQuery());
                        }
                    });
                });
    }

    public void initDataproviders(DocService docService, TextBlockService textBlockService, TagService tagService) {
        this.docService = docService;
        this.textBlockService = textBlockService;
        this.tagService = tagService;

        docDataProvider = DataProvider.fromFilteringCallbacks(query -> {
            if (query.getFilter().isPresent()) {
                return searchDocs(query.getFilter().get());
            }
            return null;
        }, query -> {
            if (query.getFilter().isPresent()) {
                return countDocs(query.getFilter().get());
            }
            return 0;
        });

        tagDataProvider = DataProvider.fromFilteringCallbacks(query -> {
            if (query.getFilter().isPresent()) {
                return searchTags(query.getFilter().get());
            }
            return null;
        }, query -> {
            if (query.getFilter().isPresent()) {
                return countTags(query.getFilter().get());
            }
            return 0;
        });
    }

    private Stream<DocSearchResult> searchDocs(String filter) {
        if (caseSensitiveCheckbox.getValue()) {
            return docService.findTop10ByRawTextContaining(filter).stream().map(doc -> new DocSearchResult(textBlockService, doc, filter));
        } else {
            return docService.findTop10ByRawTextContainingIgnoreCase(filter).stream().map(doc -> new DocSearchResult(textBlockService, doc, filter));
        }
    }

    private int countDocs(String filter) {
        if (caseSensitiveCheckbox.getValue()) {
            return (int) docService.countByRawTextContaining(filter);
        } else {
            return (int) docService.countByRawTextContainingIgnoreCase(filter);
        }
    }

    private Stream<TagSearchResult> searchTags(String filter) {
        if (caseSensitiveCheckbox.getValue()) {
            return tagService.findTop10ByNameContaining(filter).stream().map((Tag tag) -> new TagSearchResult(tag, tagService));
        } else {
            return tagService.findTop10ByNameContainingIgnoreCase(filter).stream().map((Tag tag) -> new TagSearchResult(tag, tagService));
        }
    }

    private int countTags(String filter) {
        if (caseSensitiveCheckbox.getValue()) {
            return (int) tagService.countByNameContaining(filter);
        } else {
            return (int) tagService.countByNameContainingIgnoreCase(filter);
        }
    }

    @Override
    public void open() {
        super.open();
        searchField.focus();
    }

    public Function<SearchResult, ClickNotifier> getDataViewProvider() {
        return dataViewProvider;
    }

    public void setDataViewProvider(Function<SearchResult, ClickNotifier> dataViewProvider) {
        this.dataViewProvider = dataViewProvider;
    }

    public VerticalLayout getResults() {
        return results;
    }

    public VerticalLayout getWrapper() {
        return wrapper;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public void setQueryResultListener(Consumer<SearchResult> queryResultListener) {
        this.queryResultListener = queryResultListener;
    }

    public void setCloseOnQueryResult(boolean closeOnQueryResult) {
        this.closeOnQueryResult = closeOnQueryResult;
    }

    public Button getCloseButton() {
        return closeButton;
    }
}