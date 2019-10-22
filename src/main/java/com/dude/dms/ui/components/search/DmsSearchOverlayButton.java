package com.dude.dms.ui.components.search;

import com.github.appreciated.app.layout.component.appbar.IconButton;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.DataProvider;

import java.util.function.Consumer;
import java.util.function.Function;

public class DmsSearchOverlayButton<T> extends IconButton {

    private final DmsSearchOverlayView<T> searchView;

    public DmsSearchOverlayButton() {
        this(VaadinIcon.SEARCH);
    }

    public DmsSearchOverlayButton(VaadinIcon icon) {
        this(icon.create());
    }

    public DmsSearchOverlayButton(Component icon) {
        super(icon);
        searchView = new DmsSearchOverlayView<>();
        addClickListener(event -> searchView.open());
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        searchView.getElement().removeFromParent();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        attachEvent.getUI().add(searchView);
    }

    public DmsSearchOverlayView<T> getSearchView() {
        return searchView;
    }

    public void setDataViewProvider(Function<T, ClickNotifier> provider) {
        searchView.setDataViewProvider(provider);
    }

    public void setDataProvider(DataProvider<T, String> dataProvider) {
        searchView.setDataProvider(dataProvider);
    }

    public void setQueryResultListener(Consumer<T> queryResultListener) {
        searchView.setQueryResultListener(queryResultListener);
    }

    public void setCloseOnQueryResult(boolean closeOnQueryResult) {
        searchView.setCloseOnQueryResult(closeOnQueryResult);
    }
}