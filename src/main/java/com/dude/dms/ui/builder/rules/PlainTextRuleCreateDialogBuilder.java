package com.dude.dms.ui.builder.rules;

import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.service.PlainTextRuleService;
import com.dude.dms.ui.EntityCreateListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.dialogs.PlainTextRuleCreateDialog;

public final class PlainTextRuleCreateDialogBuilder {

    private final BuilderFactory builderFactory;

    private final PlainTextRuleService plainTextRuleService;

    private EntityCreateListener<PlainTextRule> createListener;

    PlainTextRuleCreateDialogBuilder(BuilderFactory builderFactory, PlainTextRuleService plainTextRuleService) {
        this.builderFactory = builderFactory;
        this.plainTextRuleService = plainTextRuleService;
    }

    public PlainTextRuleCreateDialogBuilder withCreateListener(EntityCreateListener<PlainTextRule> createListener) {
        this.createListener = createListener;
        return this;
    }

    public PlainTextRuleCreateDialog build() {
        PlainTextRuleCreateDialog dialog = new PlainTextRuleCreateDialog(builderFactory, plainTextRuleService);
        dialog.setCreateListener(createListener);
        return dialog;
    }
}
