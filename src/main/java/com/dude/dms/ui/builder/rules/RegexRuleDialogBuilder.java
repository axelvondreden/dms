package com.dude.dms.ui.builder.rules;

import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.RegexRuleService;
import com.dude.dms.ui.EntityCreateListener;
import com.dude.dms.ui.EntityDeleteListener;
import com.dude.dms.ui.EntityEditListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.dialogs.RegexRuleDialog;

public final class RegexRuleDialogBuilder {

    private final BuilderFactory builderFactory;

    private final RegexRuleService regexRuleService;

    private RegexRule rule;

    private EntityCreateListener<RegexRule> createListener;

    private EntityEditListener<RegexRule> editListener;

    private EntityDeleteListener<RegexRule> deleteListener;

    RegexRuleDialogBuilder(BuilderFactory builderFactory, RegexRuleService regexRuleService) {
        this.builderFactory = builderFactory;
        this.regexRuleService = regexRuleService;
    }

    public RegexRuleDialogBuilder forRule(RegexRule rule) {
        this.rule = rule;
        return this;
    }

    public RegexRuleDialogBuilder withCreateListener(EntityCreateListener<RegexRule> createListener) {
        this.createListener = createListener;
        return this;
    }

    public RegexRuleDialogBuilder withEditListener(EntityEditListener<RegexRule> editListener) {
        this.editListener = editListener;
        return this;
    }

    public RegexRuleDialogBuilder withDeleteListener(EntityDeleteListener<RegexRule> deleteListener) {
        this.deleteListener = deleteListener;
        return this;
    }

    public RegexRuleDialog build() {
        RegexRuleDialog dialog = rule == null ? new RegexRuleDialog(builderFactory, regexRuleService) : new RegexRuleDialog(builderFactory, rule, regexRuleService);
        dialog.setCreateListener(createListener);
        dialog.setEditListener(editListener);
        dialog.setDeleteListener(deleteListener);
        return dialog;
    }
}
