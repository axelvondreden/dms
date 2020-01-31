package com.dude.dms.ui.components.misc

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.Icon
import dev.mett.vaadin.tooltip.Tooltips

class IconToggle(
        activeIcon: Icon,
        inactiveIcon: Icon,
        tooltip: String? = null,
        var value: Boolean = false
) : Button() {

    init {
        if (tooltip != null && tooltip.isNotEmpty()) {
            Tooltips.getCurrent().setTooltip(this, tooltip)
        }
        icon = if (value) activeIcon else inactiveIcon
        addClickListener {
            value = !value
            icon = if (value) activeIcon else inactiveIcon
        }
    }
}