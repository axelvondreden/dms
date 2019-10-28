package com.dude.dms.ui.components.dialogs;

import com.dude.dms.ui.EntityEventListener;
import com.vaadin.flow.component.dialog.Dialog;

public abstract class EventDialog extends Dialog {

    protected EntityEventListener eventListener;

    public void setEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
    }
}