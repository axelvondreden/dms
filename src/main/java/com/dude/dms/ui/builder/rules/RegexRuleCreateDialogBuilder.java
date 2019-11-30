package com.dude.dms.ui.builder.rules;

import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.RegexRuleService;
import com.dude.dms.ui.EntityCreateListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.dialogs.RegexRuleCreateDialog;

public final class RegexRuleCreateDialogBuilder {

    private final BuilderFactory builderFactory;

    private final RegexRuleService regexRuleService;

    private EntityCreateListener<RegexRule> createListener;

    RegexRuleCreateDialogBuilder(BuilderFactory builderFactory, RegexRuleService regexRuleService) {
        this.builderFactory = builderFactory;
        this.regexRuleService = regexRuleService;
    }

    public RegexRuleCreateDialogBuilder withCreateListener(EntityCreateListener<RegexRule> createListener) {
        this.createListener = createListener;
        return this;
    }

    public RegexRuleCreateDialog build() {
        RegexRuleCreateDialog dialog = new RegexRuleCreateDialog(builderFactory, regexRuleService);
        dialog.setCreateListener(createListener);
        return dialog;
    }
}
