package com.dude.dms.ui.components.misc

import com.dude.dms.brain.parsing.search.Hint
import com.dude.dms.utils.searchHintItem
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import kotlin.math.max
import kotlin.math.min


class SearchHintList : VerticalLayout() {

    private var items = emptyList<SearchHintItem>()
    private var index = -1

    var onSelect: ((Hint) -> Unit)? = null

    init {
        isPadding = false
        isSpacing = false
    }

    fun up() {
        if (index == -1) index = items.size
        index = max(-1, index - 1)
        refreshHighlighting()
    }

    fun down() {
        index = min(items.size - 1, index + 1)
        refreshHighlighting()
    }

    fun select() {
        if (index !in items.indices) return
        onSelect?.invoke(items[index].hint)
        index = -1
    }

    fun setItems(items: List<Hint>) {
        removeAll()
        this.items = items.map {
            searchHintItem(it) {
                onLeftClick {
                    onSelect?.invoke(hint)
                    index = -1
                }
            }
        }
    }

    private fun refreshHighlighting() {
        items.forEachIndexed { index, item ->
            item.style["backgroundColor"] = ""
            if (index == this.index) {
                item.style["backgroundColor"] = "var(--lumo-primary-color)"
            }
        }
    }
}