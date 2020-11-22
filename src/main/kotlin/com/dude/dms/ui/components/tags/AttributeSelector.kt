package com.dude.dms.ui.components.tags

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.brain.events.EventType
import com.dude.dms.brain.t
import com.dude.dms.utils.attributeCreateDialog
import com.dude.dms.utils.attributeService
import com.dude.dms.utils.eventManager
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.grid
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import java.util.*

class AttributeSelector : VerticalLayout() {

    private val selected = HashSet<Attribute>()

    private val available = HashSet(attributeService.findAll())

    private lateinit var selectedGrid: Grid<Attribute>

    private lateinit var availableGrid: Grid<Attribute>

    var selectedAttributes: Set<Attribute>
        get() = selected
        set(selected) {
            available.addAll(this.selected)
            this.selected.clear()
            this.selected.addAll(selected)
            available.removeAll(this.selected)
            refresh()
        }

    init {
        eventManager.register(this, Attribute::class, EventType.CREATE) {
            available.add(it)
            refresh()
        }

        horizontalLayout {
            setSizeFull()

            selectedGrid = grid {
                setItems(selected)
                minHeight = "100%"
                width = "50%"
                addColumn { "${it.name}${if (it.isRequired) "*" else ""}" }.setHeader(t("selected"))
                addThemeVariants(GridVariant.LUMO_COMPACT)
                addItemClickListener { event ->
                    selected.remove(event.item)
                    available.add(event.item)
                    refresh()
                }
            }
            availableGrid = grid {
                setItems(available)
                minHeight = "100%"
                width = "50%"
                addColumn { "${it.name}${if (it.isRequired) "*" else ""}" }.setHeader(t("available"))
                addThemeVariants(GridVariant.LUMO_COMPACT)
                addItemClickListener { event ->
                    available.remove(event.item)
                    selected.add(event.item)
                    refresh()
                }
            }
        }
        button(t("attribute"), VaadinIcon.PLUS.create()) {
            onLeftClick { attributeCreateDialog().open() }
            setWidthFull()
        }

    }

    private fun refresh() {
        selectedGrid.dataProvider.refreshAll()
        availableGrid.dataProvider.refreshAll()
    }
}