package com.dude.dms.ui.components.standard

import com.dude.dms.extensions.regexDialog
import com.github.mvysny.karibudsl.v10.icon
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField

class RegexField(label: String = "") : HorizontalLayout() {

    private lateinit var regex: TextField

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

        icon(VaadinIcon.EXPAND) {
            style["paddingBottom"] = "8px"
            onLeftClick { regexDialog(regex).open() }
        }
        regex = textField(label) { setSizeFull() }
    }
}