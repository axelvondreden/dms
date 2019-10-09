package com.dude.dms.ui.components.crud;

import com.dude.dms.backend.data.DataEntity;
import com.vaadin.flow.component.dialog.Dialog;

public abstract class CrudCreateDialog<T extends DataEntity> extends Dialog {

    protected abstract void create();

    protected CrudCreateDialog() {
    }
}