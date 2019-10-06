package com.dude.dms.ui.components;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class RegexField extends HorizontalLayout {

    private final TextField regex;

    private ValueProvider<String> reference;

    public RegexField() {
        this("");
    }

    public RegexField(String label) {
        this(label, "");
    }

    public RegexField(String label, String initialValue) {
        regex = new TextField(label, initialValue, "");
        regex.setSizeFull();
        Icon icon = VaadinIcon.EXPAND.create();
        icon.getElement().getStyle().set("paddingBottom", "8px");
        icon.addClickListener(event -> {
            if (reference != null) {
                new RegexDialog(regex, reference).open();
            } else {
                new RegexDialog(regex).open();
            }
        });

        setPadding(false);
        setMargin(false);
        setAlignItems(Alignment.END);
        add(icon, regex);
    }

    public void setValue(String value) {
        regex.setValue(value);
    }

    public String getValue() {
        return regex.getValue();
    }

    public void setReference(ValueProvider<String> reference) {
        this.reference = reference;
    }

    public boolean isEmpty() {
        return regex.isEmpty();
    }
}