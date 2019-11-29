package com.dude.dms.ui.builder.attributes;

import com.dude.dms.backend.data.docs.Attribute;
import com.dude.dms.backend.service.AttributeService;
import com.dude.dms.ui.EntityEditListener;
import com.dude.dms.ui.components.dialogs.AttributeEditDialog;

public final class AttributeEditDialogBuilder {

    private final Attribute attribute;

    private final AttributeService attributeService;

    private EntityEditListener<Attribute> editListener;

    AttributeEditDialogBuilder(Attribute attribute, AttributeService attributeService) {
        this.attribute = attribute;
        this.attributeService = attributeService;
    }

    public AttributeEditDialogBuilder withEditListener(EntityEditListener<Attribute> creditListenerateListener) {
        this.editListener = creditListenerateListener;
        return this;
    }

    public AttributeEditDialog build() {
        AttributeEditDialog dialog = new AttributeEditDialog(attribute, attributeService);
        dialog.setEditListener(editListener);
        return dialog;
    }
}