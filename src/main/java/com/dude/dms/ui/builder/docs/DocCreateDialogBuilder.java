package com.dude.dms.ui.builder.docs;

import com.dude.dms.ui.EntityCreateListener;
import com.dude.dms.ui.components.dialogs.DocCreateDialog;

public final class DocCreateDialogBuilder {

    private EntityCreateListener eventListener;

    DocCreateDialogBuilder() {
    }

    public DocCreateDialogBuilder withEventListener(EntityCreateListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public DocCreateDialog build() {
        DocCreateDialog dialog = new DocCreateDialog();
        dialog.setCreateListener(eventListener);
        return dialog;
    }
}
