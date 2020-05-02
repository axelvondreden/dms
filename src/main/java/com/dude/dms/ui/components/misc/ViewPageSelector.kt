package com.dude.dms.ui.components.misc

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select

class ViewPageSelector : HorizontalLayout() {

    private val prev = Button(VaadinIcon.ARROW_CIRCLE_LEFT.create()) { prev() }

    private val next = Button(VaadinIcon.ARROW_CIRCLE_RIGHT.create()) { next() }

    val pageSize = Select(10, 20, 30, 50, 100, 200).apply {
        value = 30
        width = "100px"
        isEmptySelectionAllowed = false
        style["padding"] = "0px 5px"
        addValueChangeListener {
            if (it.isFromClient) {
                page = 0
            }
        }
    }

    private val div = Div()

    private var silent = false

    var items = 0
        set(value) {
            if (value != field) {
                field = value
                silent = true
                page = 0
                silent = false
            }
        }

    var page = 0
        set(value) {
            field = value
            update()
        }

    val max: Int
        get() = items / pageSize.value!!

    private var onChange: ((Int) -> Unit)? = null

    init {
        isPadding = false
        isSpacing = false
        add(prev, div, next, pageSize)
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
        prev.isEnabled = page > 0
        next.isEnabled = page < max
        div.text = "${page + 1} / ${max + 1}"
        if (!silent) {
            onChange?.invoke(page)
        }
    }
}