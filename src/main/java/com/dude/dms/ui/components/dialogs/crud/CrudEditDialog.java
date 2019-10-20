package com.dude.dms.ui.components.dialogs.crud;

import com.dude.dms.backend.data.DataEntity;
import com.dude.dms.ui.EntityEventListener;
import com.vaadin.flow.component.dialog.Dialog;

public abstract class CrudEditDialog<T extends DataEntity> extends Dialog {

    protected abstract void save();

    public abstract void open(T item);

    protected EntityEventListener eventListener;

    protected CrudEditDialog() {

    }

    public void setEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
    }
}