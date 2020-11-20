package com.dude.dms.ui.components.misc

import com.github.mvysny.karibudsl.v10.label
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import kotlin.math.max
import kotlin.math.min


class SearchHintList : VerticalLayout() {

    private var items = emptyList<Label>()
    private var index = -1

    var onSelect: ((String) -> Unit)? = null

    init {
        isPadding = false
        isSpacing = false
    }

    fun up() {
        index = max(-1, index - 1)
        refreshHighlighting()
    }

    fun down() {
        index = min(items.size - 1, index + 1)
        refreshHighlighting()
    }

    fun select() {
        if (index !in items.indices) return
        onSelect?.invoke(items[index].text)
        index = -1
    }

    fun setItems(items: List<String>) {
        removeAll()
        this.items = items.map {
            label(it) { setWidthFull() }
        }
    }

    private fun refreshHighlighting() {
        items.forEachIndexed { index, text ->
            text.element.style["border"] = ""
            if (index == this.index) {
                text.element.style["border"] = "2px solid green"
            }
        }
    }
}