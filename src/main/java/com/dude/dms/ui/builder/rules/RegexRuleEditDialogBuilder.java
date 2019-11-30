package com.dude.dms.ui.builder.rules;

import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.RegexRuleService;
import com.dude.dms.ui.EntityDeleteListener;
import com.dude.dms.ui.EntityEditListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.dialogs.RegexRuleEditDialog;

public final class RegexRuleEditDialogBuilder {

    private final BuilderFactory builderFactory;

    private final RegexRuleService regexRuleService;

    private final RegexRule rule;

    private EntityEditListener<RegexRule> editListener;

    private EntityDeleteListener<RegexRule> deleteListener;

    RegexRuleEditDialogBuilder(BuilderFactory builderFactory, RegexRule rule, RegexRuleService regexRuleService) {
        this.builderFactory = builderFactory;
        this.rule = rule;
        this.regexRuleService = regexRuleService;
    }

    public RegexRuleEditDialogBuilder withEditListener(EntityEditListener<RegexRule> editListener) {
        this.editListener = editListener;
        return this;
    }

    public RegexRuleEditDialogBuilder withDeleteListener(EntityDeleteListener<RegexRule> deleteListener) {
        this.deleteListener = deleteListener;
        return this;
    }

    public RegexRuleEditDialog build() {
        RegexRuleEditDialog dialog = new RegexRuleEditDialog(builderFactory, rule, regexRuleService);
        dialog.setEditListener(editListener);
        dialog.setDeleteListener(deleteListener);
        return dialog;
    }
}
