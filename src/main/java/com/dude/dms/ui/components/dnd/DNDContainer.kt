package com.dude.dms.ui.components.dnd

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import kotlin.math.abs

class DNDContainer(vararg components: Component) : HorizontalLayout() {

    private var items = sortedSetOf<DNDItem>().toSortedSet()

    init {
        isSpacing = false
        setWidthFull()
        alignItems = FlexComponent.Alignment.CENTER

        components.forEachIndexed { index, component -> items.add(DNDComponent(component, index * 2 + 1) { switch(it) }) }
        addSpacers()

        fill()
    }

    private fun addSpacers() {
        (0..items.size).forEach { index -> items.add(DNDSpacer(index * 2) { switch(it) }) }
    }

    fun fill() {
        removeAll()
        add(*items.sorted().toTypedArray())
    }

    private fun switch(pair: Pair<Int, Int>) {
        if (abs(pair.first - pair.second) < 2) return
        val item1 = items.first { it.index == pair.first }
        val item2 = items.first { it.index == pair.second }
        item1.index = pair.second
        item2.index = pair.first
        items.removeIf { it is DNDSpacer }
        items.sorted().forEachIndexed { index, item ->
            item.index = index * 2 + 1
            (item as DNDComponent).dragSource.dragData = item.index
        }
        addSpacers()
        fill()
    }
}