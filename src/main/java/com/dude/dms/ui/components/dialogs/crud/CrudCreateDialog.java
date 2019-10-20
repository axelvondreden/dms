package com.dude.dms.ui.components.dialogs.crud;

import com.dude.dms.backend.data.DataEntity;
import com.dude.dms.ui.EntityEventListener;
import com.vaadin.flow.component.dialog.Dialog;

public abstract class CrudCreateDialog<T extends DataEntity> extends Dialog {

    protected abstract void create();

    protected EntityEventListener eventListener;

    protected CrudCreateDialog() {

    }

    public void setEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
    }
}