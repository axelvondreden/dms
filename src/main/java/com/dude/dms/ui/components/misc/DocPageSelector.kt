package com.dude.dms.ui.components.misc

import com.github.mvysny.karibudsl.v10.iconButton
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.select
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select

class DocPageSelector(max: Int = 1) : HorizontalLayout() {

    private var prev: Button

    private var next: Button

    private var current: Select<Int>

    var page: Int = 1
        set(value) {
            field = value
            current.value = value
            update()
        }

    var max = max
        set(value) {
            field = value
            current.setItems((1..value).toList())
        }

    private var onChange: ((Int) -> Unit)? = null

    init {
        isPadding = false
        isSpacing = false

        prev = iconButton(VaadinIcon.ARROW_CIRCLE_LEFT.create()) {
            onLeftClick { prev() }
        }
        current = select {
            setItems(*(1..max).toList().toTypedArray())
            value = page
            width = "80px"
            isEmptySelectionAllowed = false
            style["padding"] = "0px 5px"
            addValueChangeListener { if (it.isFromClient) page = it.value }
        }
        next = iconButton(VaadinIcon.ARROW_CIRCLE_RIGHT.create()) {
            onLeftClick { next() }
        }
    }

    fun setChangeListener(onChange: ((Int) -> Unit)?) {
        this.onChange = onChange
    }

    private fun prev() {
        if (page > 0) page--
    }

    private fun next() {
        if (page < max) page++
    }

    private fun update() {
        prev.isEnabled = page > 1
        next.isEnabled = page < max
        onChange?.invoke(page)
    }
}