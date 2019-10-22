package com.dude.dms.ui.components.search;

import com.dude.dms.backend.service.DocService;
import com.vaadin.flow.component.ClickNotifier;

import java.util.function.Consumer;
import java.util.function.Function;

public class DmsSearchOverlayButtonBuilder {

    private Function<SearchResult, ClickNotifier> dataViewProvider;

    private Consumer<SearchResult> queryResultListener;

    private Boolean closeOnQueryResult;

    private final DocService docService;

    public DmsSearchOverlayButtonBuilder(DocService docService) {
        this.docService = docService;
    }

    public DmsSearchOverlayButtonBuilder withDataViewProvider(Function<SearchResult, ClickNotifier> dataViewProvider) {
        this.dataViewProvider = dataViewProvider;
        return this;
    }

    public DmsSearchOverlayButton build() {
        DmsSearchOverlayButton appBarSearchButton = new DmsSearchOverlayButton();
        appBarSearchButton.setDataViewProvider(dataViewProvider);
        appBarSearchButton.setQueryResultListener(queryResultListener);
        if (closeOnQueryResult != null) {
            appBarSearchButton.setCloseOnQueryResult(closeOnQueryResult);
        }
        appBarSearchButton.getSearchView().initDataprovider(docService);;
        return appBarSearchButton;
    }

    public DmsSearchOverlayButtonBuilder withQueryResultListener(Consumer<SearchResult> queryResultListener) {
        this.queryResultListener = queryResultListener;
        return this;
    }

    public DmsSearchOverlayButtonBuilder withCloseOnQueryResult(boolean closeOnQueryResult) {
        this.closeOnQueryResult = closeOnQueryResult;
        return this;
    }
}