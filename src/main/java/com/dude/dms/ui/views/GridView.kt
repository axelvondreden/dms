package com.dude.dms.ui.views

import com.dude.dms.backend.data.DataEntity
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.VerticalLayout

abstract class GridView<T> : VerticalLayout() where T : DataEntity {

    protected val grid: Grid<T>

    init {
        setSizeFull()
        isPadding = false
        isSpacing = false
        grid = Grid()
        grid.setSizeFull()
        add(grid)
    }
}