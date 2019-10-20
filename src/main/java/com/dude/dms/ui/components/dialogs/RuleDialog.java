package com.dude.dms.ui.components.dialogs;

import com.dude.dms.ui.EntityEventListener;
import com.vaadin.flow.component.dialog.Dialog;

public abstract class RuleDialog extends Dialog {

    protected abstract void save();

    protected abstract void delete();

    protected EntityEventListener eventListener;

    protected RuleDialog() {

    }

    public void setEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
    }
}