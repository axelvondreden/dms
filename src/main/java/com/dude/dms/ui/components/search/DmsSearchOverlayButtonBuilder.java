package com.dude.dms.ui.components.search;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.data.provider.DataProvider;

import java.util.function.Consumer;
import java.util.function.Function;

public class DmsSearchOverlayButtonBuilder<T> {

    private Function<T, ClickNotifier> dataViewProvider;
    private DataProvider<T, String> dataProvider;
    private Consumer<T> queryResultListener;
    private Boolean closeOnQueryResult;

    public DmsSearchOverlayButtonBuilder() {
    }

    public DmsSearchOverlayButtonBuilder<T> withDataViewProvider(Function<T, ClickNotifier> dataViewProvider) {
        this.dataViewProvider = dataViewProvider;
        return this;
    }

    public DmsSearchOverlayButtonBuilder<T> withDataProvider(DataProvider<T, String> dataProvider) {
        this.dataProvider = dataProvider;
        return this;
    }

    public DmsSearchOverlayButton<T> build() {
        DmsSearchOverlayButton<T> appBarSearchButton = new DmsSearchOverlayButton<>();
        appBarSearchButton.setDataViewProvider(dataViewProvider);
        appBarSearchButton.setDataProvider(dataProvider);
        appBarSearchButton.setQueryResultListener(queryResultListener);
        if (closeOnQueryResult != null) {
            appBarSearchButton.setCloseOnQueryResult(closeOnQueryResult);
        }
        return appBarSearchButton;
    }

    public DmsSearchOverlayButtonBuilder<T> withQueryResultListener(Consumer<T> queryResultListener) {
        this.queryResultListener = queryResultListener;
        return this;
    }

    public DmsSearchOverlayButtonBuilder<T> withCloseOnQueryResult(boolean closeOnQueryResult) {
        this.closeOnQueryResult = closeOnQueryResult;
        return this;
    }
}