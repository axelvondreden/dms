package com.dude.dms.ui.components.misc;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import dev.mett.vaadin.tooltip.Tooltips;

public class IconToggle extends Button {

    private boolean active;

    public IconToggle(Icon activeIcon, Icon inactiveIcon, String tooltip) {
        active = false;
        if (tooltip != null && !tooltip.isEmpty()) {
            Tooltips.getCurrent().setTooltip(this, tooltip);
        }

        setIcon(inactiveIcon);
        addClickListener(event -> {
            active = !active;
            setIcon(active ? activeIcon : inactiveIcon);
        });
    }

    public boolean getValue() {
        return active;
    }
}