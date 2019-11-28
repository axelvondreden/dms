package com.dude.dms.ui.components.misc;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import dev.mett.vaadin.tooltip.Tooltips;

public class IconCheckbox extends HorizontalLayout {

    private final Checkbox checkbox;

    public IconCheckbox(String label, String tooltip, VaadinIcon icon) {
        setMargin(false);
        setPadding(false);

        checkbox = new Checkbox(label);
        if (tooltip != null && !tooltip.isEmpty()) {
            Tooltips.getCurrent().setTooltip(this, tooltip);
        }
        add(checkbox, icon.create());
        addClickListener(event -> checkbox.setValue(!checkbox.getValue()));
    }

    public boolean getValue() {
        return checkbox.getValue();
    }
}