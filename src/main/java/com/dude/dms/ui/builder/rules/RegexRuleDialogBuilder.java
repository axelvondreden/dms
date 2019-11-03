package com.dude.dms.ui.builder.rules;

import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.RegexRuleService;
import com.dude.dms.ui.EntityEventListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.dialogs.RegexRuleDialog;

public final class RegexRuleDialogBuilder {

    private final BuilderFactory builderFactory;

    private final RegexRuleService regexRuleService;

    private RegexRule rule;

    private EntityEventListener eventListener;

    RegexRuleDialogBuilder(BuilderFactory builderFactory, RegexRuleService regexRuleService) {
        this.builderFactory = builderFactory;
        this.regexRuleService = regexRuleService;
    }

    public RegexRuleDialogBuilder forRule(RegexRule rule) {
        this.rule = rule;
        return this;
    }

    public RegexRuleDialogBuilder withEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public RegexRuleDialog build() {
        RegexRuleDialog dialog = rule == null ? new RegexRuleDialog(builderFactory, regexRuleService) : new RegexRuleDialog(builderFactory, rule, regexRuleService);
        dialog.setEventListener(eventListener);
        return dialog;
    }
}
