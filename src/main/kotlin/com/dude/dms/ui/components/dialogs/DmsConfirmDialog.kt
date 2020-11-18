package com.dude.dms.ui.components.dialogs

import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon

class DmsConfirmDialog(
        message: String,
        confirmText: String,
        icon: VaadinIcon,
        theme: ButtonVariant,
        event: (ClickEvent<Button>) -> Unit
) : DmsDialog(message) {

    init {
        button(confirmText, icon.create()) {
            onLeftClick { event.invoke(it) }
            addThemeVariants(theme)
            addClickListener { close() }
        }
    }
}