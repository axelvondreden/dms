package com.dude.dms.ui.components.dialogs;

import com.dude.dms.ui.EntityEventListener;
import com.vaadin.flow.component.dialog.Dialog;

import java.util.Optional;

public abstract class RuleDialog extends Dialog {

    protected abstract void save();

    protected abstract void delete();

    protected Optional<EntityEventListener> eventListener;

    protected RuleDialog() {
        eventListener = Optional.empty();
    }

    public void setEventListener(EntityEventListener eventListener) {
        this.eventListener = Optional.of(eventListener);
    }
}