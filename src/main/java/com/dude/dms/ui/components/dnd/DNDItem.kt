package com.dude.dms.ui.components.dnd

import com.vaadin.flow.component.dnd.DropEffect
import com.vaadin.flow.component.dnd.DropTarget
import com.vaadin.flow.component.html.Div

abstract class DNDItem(var index: Int, private val onDrop: (Pair<Int, Int>) -> Unit) : Div(), Comparable<DNDItem> {

    init {
        style["display"] = "flex"
        style["cursor"] = "grab"

        val dropTarget = DropTarget.create(this)
        dropTarget.dropEffect = DropEffect.MOVE
        dropTarget.addDropListener {
            onDrop.invoke(index to it.dragData.get() as Int)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DNDItem
        if (index != other.index) return false
        return true
    }

    override fun hashCode() = index

    override fun compareTo(other: DNDItem) = index.compareTo(other.index)
}