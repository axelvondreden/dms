package com.dude.dms.ui.components.search;

import com.github.appreciated.app.layout.component.appbar.IconButton;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.icon.VaadinIcon;

public class DmsSearchOverlayButton extends IconButton {

    private final DmsSearchOverlayView searchView;

    public DmsSearchOverlayButton() {
        this(VaadinIcon.SEARCH);
    }

    public DmsSearchOverlayButton(VaadinIcon icon) {
        this(icon.create());
    }

    public DmsSearchOverlayButton(Component icon) {
        super(icon);
        searchView = new DmsSearchOverlayView();
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

    public DmsSearchOverlayView getSearchView() {
        return searchView;
    }
}