package com.dude.dms.ui.components.misc

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.VaadinIcon

class ConfirmDialog(
        message: String,
        confirmText: String,
        icon: VaadinIcon,
        theme: ButtonVariant,
        event: ComponentEventListener<ClickEvent<Button>>
) : Dialog() {

    init {
        add(Label(message))
        add(Button(confirmText, icon.create(), event).apply { addThemeVariants(theme) })
    }
}