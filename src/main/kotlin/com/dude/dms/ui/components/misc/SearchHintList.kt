package com.dude.dms.ui.components.misc

import com.github.mvysny.karibudsl.v10.div
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import kotlin.math.max
import kotlin.math.min


class SearchHintList : VerticalLayout() {

    private var items = emptyList<Div>()
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
            div {
                text = it
                setWidthFull()
                style["paddingLeft"] = "4px"
                onLeftClick {
                    onSelect?.invoke(text)
                    index = -1
                }
            }
        }
    }

    private fun refreshHighlighting() {
        items.forEachIndexed { index, div ->
            div.style["backgroundColor"] = ""
            if (index == this.index) {
                div.style["backgroundColor"] = "var(--lumo-primary-color)"
            }
        }
    }
}