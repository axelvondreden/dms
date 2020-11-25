package com.dude.dms.ui.components.misc

import com.dude.dms.brain.search.hint.Hint
import com.dude.dms.brain.search.hint.HintResult
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
        style["opacity"] = "0.95"
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

    fun select(hint: Hint) {
        items.firstOrNull { it.hint == hint }?.let { onSelect?.invoke(it.hint) }
        index = -1
    }

    fun setHints(result: HintResult) {
        removeAll()
        this.items = result.hints.map {
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