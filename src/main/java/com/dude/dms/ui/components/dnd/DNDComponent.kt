package com.dude.dms.ui.components.dnd

import com.github.appreciated.card.Card
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.dnd.DragSource


class DNDComponent(item: Component, index: Int, onDrop: (Pair<Int, Int>) -> Unit) : DNDItem(index, onDrop) {

    val dragSource = DragSource.create(this)

    init {
        add(Card(item).apply {
            height = "60px"
            content.style["flexDirection"] = "row"
            content.style["alignItems"] = "center"
            content.style["padding"] = "0px 10px"
        })

        dragSource.dragData = index
    }
}