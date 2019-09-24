package com.dude.dms.ui.views;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

public interface HasConfirmation {

    void setConfirmDialog(ConfirmDialog confirmDialog);

    ConfirmDialog getConfirmDialog();
}
