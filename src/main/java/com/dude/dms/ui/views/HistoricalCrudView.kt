package com.dude.dms.ui.views

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.Diffable
import com.dude.dms.backend.data.Historical
import com.dude.dms.backend.data.history.History
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.function.ValueProvider

abstract class HistoricalCrudView<T, U : History> : VerticalLayout() where T : DataEntity, T : Historical<U>, T : Diffable<T> {

    protected val grid: Grid<T>

    init {
        setSizeFull()
        isPadding = false
        isSpacing = false
        grid = Grid()
        grid.setSizeFull()
        add(grid)
    }

    /**
     * Adds a new column to the grid
     *
     * @param header        grid header
     * @param valueProvider component for column
     */
    protected fun addComponentColumn(header: String, valueProvider: ValueProvider<T, out Component>) {
        grid.addComponentColumn(valueProvider).setHeader(header).setResizable(true).isAutoWidth = true
    }

    /**
     * Adds a new column to the grid
     *
     * @param header        grid header
     * @param valueProvider component for column
     */
    protected fun addColumn(header: String, valueProvider: ValueProvider<T, *>) {
        grid.addColumn(valueProvider).setHeader(header).setResizable(true).isAutoWidth = true
    }
}