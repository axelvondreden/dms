package com.dude.dms.ui.components.standard

import com.dude.dms.ui.components.dialogs.RegexDialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField

class RegexField(label: String = "", initialValue: String = "") : HorizontalLayout() {

    private val regex = TextField(label, initialValue, "").apply { setSizeFull() }

    var value: String?
        get() = regex.value
        set(value) {
            regex.value = value
        }

    val isEmpty: Boolean
        get() = regex.isEmpty

    init {
        isPadding = false
        isMargin = false
        alignItems = FlexComponent.Alignment.END

        val icon = VaadinIcon.EXPAND.create().apply {
            element.style["paddingBottom"] = "8px"
            addClickListener { RegexDialog(regex).open() }
        }
        add(icon, regex)
    }
}