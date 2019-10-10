package com.dude.dms.ui.components.crud;

import com.dude.dms.backend.data.DataEntity;
import com.dude.dms.ui.EntityEventListener;
import com.vaadin.flow.component.dialog.Dialog;

import java.util.Optional;

public abstract class CrudEditDialog<T extends DataEntity> extends Dialog {

    protected abstract void save();

    public abstract void open(T item);

    protected Optional<EntityEventListener> eventListener;

    protected CrudEditDialog() {
        eventListener = Optional.empty();
    }

    public void setEventListener(EntityEventListener eventListener) {
        this.eventListener = Optional.of(eventListener);
    }
}