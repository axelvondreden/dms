package com.dude.dms.ui.builder.attributes;

import com.dude.dms.backend.data.docs.Attribute;
import com.dude.dms.backend.service.AttributeService;
import com.dude.dms.ui.EntityCreateListener;
import com.dude.dms.ui.components.dialogs.AttributeCreateDialog;

public final class AttributeCreateDialogBuilder {

    private final AttributeService attributeService;

    private EntityCreateListener<Attribute> createListener;

    AttributeCreateDialogBuilder(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    public AttributeCreateDialogBuilder withCreateListener(EntityCreateListener<Attribute> createListener) {
        this.createListener = createListener;
        return this;
    }

    public AttributeCreateDialog build() {
        AttributeCreateDialog dialog = new AttributeCreateDialog(attributeService);
        dialog.setCreateListener(createListener);
        return dialog;
    }
}
