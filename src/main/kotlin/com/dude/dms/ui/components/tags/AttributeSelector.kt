package com.dude.dms.ui.components.tags

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.brain.events.EventType
import com.dude.dms.brain.t
import com.dude.dms.utils.attributeService
import com.dude.dms.utils.eventManager
import com.github.mvysny.karibudsl.v10.grid
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class AttributeSelector : VerticalLayout() {

    private val grid: Grid<Attribute>

    var selectedAttributes: Set<Attribute>
        get() = grid.asMultiSelect().selectedItems
        set(selected) {
            grid.asMultiSelect().deselectAll()
            grid.asMultiSelect().select(selected)
        }

    init {
        isPadding = false
        eventManager.register(this, Attribute::class, EventType.CREATE) {
            refresh()
        }

        grid = grid {
            setItems(attributeService.findAll())
            setSizeFull()
            setSelectionMode(Grid.SelectionMode.MULTI)
            addColumn { "${it.name}${if (it.isRequired) "*" else ""}" }.setHeader(t("attributes"))
            addThemeVariants(GridVariant.LUMO_COMPACT)
        }
    }

    private fun refresh() {
        val items = selectedAttributes
        grid.setItems(attributeService.findAll())
        grid.asMultiSelect().select(items)
    }
}
