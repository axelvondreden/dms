package com.dude.dms.ui.components.misc;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import dev.mett.vaadin.tooltip.Tooltips;

public class IconToggle extends Button {

    private boolean active;

    public IconToggle(Icon activeIcon, Icon inactiveIcon, String tooltip) {
        this(activeIcon, inactiveIcon, tooltip, false);
    }

    public IconToggle(Icon activeIcon, Icon inactiveIcon, String tooltip, boolean initialValue) {
        active = initialValue;
        if (tooltip != null && !tooltip.isEmpty()) {
            Tooltips.getCurrent().setTooltip(this, tooltip);
        }

        setIcon(active ? activeIcon : inactiveIcon);
        addClickListener(event -> {
            active = !active;
            setIcon(active ? activeIcon : inactiveIcon);
        });
    }

    public boolean getValue() {
        return active;
    }
}