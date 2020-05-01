package com.dude.dms.ui.components.misc

import com.dude.dms.ui.components.dialogs.DmsDialog
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon

class ConfirmDialog(
        message: String,
        confirmText: String,
        icon: VaadinIcon,
        theme: ButtonVariant,
        event: ComponentEventListener<ClickEvent<Button>>
) : DmsDialog(message) {

    init {
        add(Button(confirmText, icon.create(), event).apply {
            addThemeVariants(theme)
            addClickListener { close() }
        })
    }
}