package com.dude.dms.ui.builder.dialogs;

import com.dude.dms.ui.EntityEventListener;
import com.dude.dms.ui.components.dialogs.DocCreateDialog;

public final class DocCreateDialogBuilder {

    private EntityEventListener eventListener;

    DocCreateDialogBuilder() {
    }

    public DocCreateDialogBuilder withEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public DocCreateDialog build() {
        DocCreateDialog dialog = new DocCreateDialog();
        dialog.setEventListener(eventListener);
        return dialog;
    }
}
