package com.dude.dms.ui.components.misc;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;

public class ConfirmDialog extends Dialog {

    public ConfirmDialog(String message, String confirmText, VaadinIcon icon, ComponentEventListener<ClickEvent<Button>> event, ButtonVariant theme) {
        add(new Label(message));
        Button button = new Button(confirmText, icon.create(), event);
        button.addThemeVariants(theme);
        add(button);
    }
}