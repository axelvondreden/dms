package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.data.entity.Historical;
import com.dude.dms.backend.data.entity.History;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;

public abstract class HistoricalCrudView<T extends Historical<U>, U extends History> extends SplitLayout implements AfterNavigationObserver {

    protected Grid<T> grid;

    protected HistoricalCrudForm<T, U> crudForm;

    protected abstract void setColumns();

    protected abstract void fillGrid();

    protected abstract void attachBinder();

    protected HistoricalCrudView() {
        setSizeFull();
        setOrientation(Orientation.HORIZONTAL);

        grid = new Grid<>();
        grid.setSizeFull();
        setColumns();
        grid.asSingleSelect().addValueChangeListener(event -> fillForm(event.getValue()));
        addToPrimary(grid);

        crudForm = new HistoricalCrudForm<>();
        addToSecondary(crudForm);
    }

    protected void fillForm(T entity) {
        crudForm.getBinder().readBean(entity);
    }

    protected void clearForm() {
        crudForm.getBinder().readBean(null);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        fillGrid();
        if (!crudForm.getBinder().getFields().findAny().isPresent()) {
            attachBinder();
        }
    }

}
