package com.dude.dms.ui.builder.rules;

import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.service.PlainTextRuleService;
import com.dude.dms.ui.EntityCreateListener;
import com.dude.dms.ui.EntityDeleteListener;
import com.dude.dms.ui.EntityEditListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.dialogs.PlainTextRuleDialog;

public final class PlainTextRuleDialogBuilder {

    private final BuilderFactory builderFactory;

    private final PlainTextRuleService plainTextRuleService;

    private PlainTextRule rule;

    private EntityCreateListener<PlainTextRule> createListener;

    private EntityEditListener<PlainTextRule> editListener;

    private EntityDeleteListener<PlainTextRule> deleteListener;

    PlainTextRuleDialogBuilder(BuilderFactory builderFactory, PlainTextRuleService plainTextRuleService) {
        this.builderFactory = builderFactory;
        this.plainTextRuleService = plainTextRuleService;
    }

    public PlainTextRuleDialogBuilder forRule(PlainTextRule rule) {
        this.rule = rule;
        return this;
    }

    public PlainTextRuleDialogBuilder withCreateListener(EntityCreateListener<PlainTextRule> createListener) {
        this.createListener = createListener;
        return this;
    }

    public PlainTextRuleDialogBuilder withEditListener(EntityEditListener<PlainTextRule> editListener) {
        this.editListener = editListener;
        return this;
    }

    public PlainTextRuleDialogBuilder withDeleteListener(EntityDeleteListener<PlainTextRule> deleteListener) {
        this.deleteListener = deleteListener;
        return this;
    }

    public PlainTextRuleDialog build() {
        PlainTextRuleDialog dialog = rule == null ? new PlainTextRuleDialog(builderFactory, plainTextRuleService) : new PlainTextRuleDialog(builderFactory, rule, plainTextRuleService);
        dialog.setCreateListener(createListener);
        dialog.setEditListener(editListener);
        dialog.setDeleteListener(deleteListener);
        return dialog;
    }
}
