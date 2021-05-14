package com.dude.dms.ui.components.misc

import com.dude.dms.ui.EditMode
import com.github.mvysny.karibudsl.v10.select
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.FlexLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.function.SerializableFunction
import com.vaadin.flow.server.Command

class ModeSelector : HorizontalLayout() {

    private var mode: EditMode = EditMode.EDIT
        set(value) {
            field = value
            current.value = value
            update()
        }

    private var current: Select<EditMode>

    private var onChange: ((EditMode) -> Unit)? = null

    init {
        isPadding = false
        isSpacing = false

        current = select {
            setItems(*EditMode.values())
            value = mode
            width = "120px"
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
        mode = EditMode.EDIT
        EditMode.values().forEach { UI.getCurrent().addShortcutListener(Command { mode = it }, it.shortcut, KeyModifier.CONTROL.apply {  }) }
    }

    fun setChangeListener(onChange: ((EditMode) -> Unit)?) {
        this.onChange = onChange
    }

    private fun update() {
        onChange?.invoke(mode)
    }
}
