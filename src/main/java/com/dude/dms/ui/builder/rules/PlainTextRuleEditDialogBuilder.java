package com.dude.dms.ui.builder.rules;

import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.service.PlainTextRuleService;
import com.dude.dms.ui.EntityDeleteListener;
import com.dude.dms.ui.EntityEditListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.dialogs.PlainTextRuleEditDialog;

public final class PlainTextRuleEditDialogBuilder {

    private final BuilderFactory builderFactory;

    private final PlainTextRuleService plainTextRuleService;

    private final PlainTextRule rule;

    private EntityEditListener<PlainTextRule> editListener;

    private EntityDeleteListener<PlainTextRule> deleteListener;

    PlainTextRuleEditDialogBuilder(BuilderFactory builderFactory, PlainTextRule rule, PlainTextRuleService plainTextRuleService) {
        this.builderFactory = builderFactory;
        this.rule = rule;
        this.plainTextRuleService = plainTextRuleService;
    }

    public PlainTextRuleEditDialogBuilder withEditListener(EntityEditListener<PlainTextRule> editListener) {
        this.editListener = editListener;
        return this;
    }

    public PlainTextRuleEditDialogBuilder withDeleteListener(EntityDeleteListener<PlainTextRule> deleteListener) {
        this.deleteListener = deleteListener;
        return this;
    }

    public PlainTextRuleEditDialog build() {
        PlainTextRuleEditDialog dialog = new PlainTextRuleEditDialog(builderFactory, rule, plainTextRuleService);
        dialog.setEditListener(editListener);
        dialog.setDeleteListener(deleteListener);
        return dialog;
    }
}
