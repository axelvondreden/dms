package com.dude.dms.ui.components.tags

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.grid.ItemClickEvent
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import java.util.*

class AttributeSelector(builderFactory: BuilderFactory, attributeService: AttributeService, eventManager: EventManager) : VerticalLayout() {

    private val selected = HashSet<Attribute>()

    private val available = HashSet(attributeService.findAll())

    private val selectedGrid = Grid<Attribute>().apply {
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

    private val availableGrid = Grid<Attribute>().apply {
        setItems(available)
        minHeight = "100%"
        width = "50%"
        addColumn { "${it.name}${if (it.isRequired) "*" else ""}" }.setHeader(t("available"))
        addThemeVariants(GridVariant.LUMO_COMPACT)
        addItemClickListener { event: ItemClickEvent<Attribute> ->
            available.remove(event.item)
            selected.add(event.item)
            refresh()
        }
    }

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
        val listWrapper = HorizontalLayout(selectedGrid, availableGrid).apply { setSizeFull() }
        val addButton = Button(t("attribute"), VaadinIcon.PLUS.create()) {
            builderFactory.attributes().createDialog().build().open()
        }
        addButton.setWidthFull()
        add(listWrapper, addButton)

        eventManager.register(this, Attribute::class, EventType.CREATE) {
            available.add(it)
            refresh()
        }
    }

    private fun refresh() {
        selectedGrid.dataProvider.refreshAll()
        availableGrid.dataProvider.refreshAll()
    }
}