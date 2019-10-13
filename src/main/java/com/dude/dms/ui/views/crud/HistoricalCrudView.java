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

public abstract class HistoricalCrudView<T extends DataEntity & Historical<U> & Diffable<T>, U extends History> extends VerticalLayout {

    protected Grid<T> grid;

    protected final HistoricalCrudService<T, U> service;

    protected HistoricalCrudView(HistoricalCrudService<T, U> service) {
        this.service = service;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        grid = new Grid<>();
        grid.setSizeFull();
        add(grid);
    }

    /**
     * Adds a new column to the grid
     *
     * @param header        grid header
     * @param valueProvider component for column
     */
    protected void addComponentColumn(String header, ValueProvider<T, ? extends Component> valueProvider) {
        grid.addComponentColumn(valueProvider).setHeader(header).setResizable(true).setAutoWidth(true);
    }

    /**
     * Adds a new column to the grid
     *
     * @param header        grid header
     * @param valueProvider component for column
     */
    protected void addColumn(String header, ValueProvider<T, ?> valueProvider) {
        grid.addColumn(valueProvider).setHeader(header).setResizable(true).setAutoWidth(true);
    }
}