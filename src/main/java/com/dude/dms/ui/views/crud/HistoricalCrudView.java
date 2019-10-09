package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.data.DataEntity;
import com.dude.dms.backend.data.Diffable;
import com.dude.dms.backend.data.Historical;
import com.dude.dms.backend.data.history.History;
import com.dude.dms.backend.service.HistoricalCrudService;
import com.dude.dms.ui.components.crud.CrudEditDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;

public abstract class HistoricalCrudView<T extends DataEntity & Historical<U> & Diffable<T>, U extends History> extends VerticalLayout implements AfterNavigationObserver {

    protected Grid<T> grid;

    protected final HistoricalCrudService<T, U> service;

    protected abstract void defineProperties();

    protected HistoricalCrudView(HistoricalCrudService<T, U> service) {
        this.service = service;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        grid = new Grid<>();
        grid.setSizeFull();
        add(grid);
    }

    protected void fillGrid() {
        grid.setItems(service.findAll());
    }

    /**
     * Adds a new column to the grid
     *
     * @param header        grid header
     * @param valueProvider component for column
     */
    protected void addComponentColumn(String header, ValueProvider<T, ? extends Component> valueProvider) {
        grid.addComponentColumn(valueProvider).setHeader(header);
    }

    /**
     * Adds a new column to the grid
     *
     * @param header        grid header
     * @param valueProvider component for column
     */
    protected void addColumn(String header, ValueProvider<T, ?> valueProvider) {
        grid.addColumn(valueProvider).setHeader(header);
    }

    protected void addEditDialog(CrudEditDialog<T> editDialog) {
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (!event.getHasValue().isEmpty()) {
                editDialog.open(event.getValue());
            }
        });
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.removeAllColumns();
        fillGrid();
        defineProperties();
    }
}