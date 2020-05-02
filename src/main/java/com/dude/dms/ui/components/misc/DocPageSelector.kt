package com.dude.dms.ui.components.misc

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select

class DocPageSelector(max: Int = 1) : HorizontalLayout() {

    private val prev = Button(VaadinIcon.ARROW_CIRCLE_LEFT.create()) { prev() }

    private val next = Button(VaadinIcon.ARROW_CIRCLE_RIGHT.create()) { next() }

    private val current = Select(*(1..max).toList().toTypedArray()).apply {
        value = page
        width = "80px"
        isEmptySelectionAllowed = false
        style["padding"] = "0px 5px"
        addValueChangeListener { if (it.isFromClient) page = it.value }
    }

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
        add(prev, current, next)
    }

    fun setChangeListener(onChange: ((Int) -> Unit)?) {
        this.onChange = onChange
    }

    fun prev() {
        if (page > 0) page--
    }

    fun next() {
        if (page < max) page++
    }

    private fun update() {
        prev.isEnabled = page > 1
        next.isEnabled = page < max
        onChange?.invoke(page)
    }
}