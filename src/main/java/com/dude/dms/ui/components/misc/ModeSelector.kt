package com.dude.dms.ui.components.misc

import com.dude.dms.ui.EditMode
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.FlexLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.function.SerializableFunction

class ModeSelector : HorizontalLayout() {

    var mode: EditMode = EditMode.EDIT
        set(value) {
            field = value
            current.value = value
            update()
        }

    private val current = Select(*EditMode.values()).apply {
        value = mode
        width = "80px"
        isEmptySelectionAllowed = false
        style["padding"] = "0px 5px"
        addValueChangeListener { if (it.isFromClient) mode = it.value }

        setRenderer(ComponentRenderer(SerializableFunction {
            FlexLayout(it.icon.create().apply { style["maxHeight"] = "18px" }, Div().apply {
                text = it.uiName
                style["margin-left"] = "0.5em"
            })
        }))
    }

    private var onChange: ((EditMode) -> Unit)? = null

    init {
        isPadding = false
        isSpacing = false
        add(current)
        mode = EditMode.EDIT
    }

    fun setChangeListener(onChange: ((EditMode) -> Unit)?) {
        this.onChange = onChange
    }

    private fun update() {
        onChange?.invoke(mode)
    }
}