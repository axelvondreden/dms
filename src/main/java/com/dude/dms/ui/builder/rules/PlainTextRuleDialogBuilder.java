package com.dude.dms.ui.builder.rules;

import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.service.PlainTextRuleService;
import com.dude.dms.ui.EntityEventListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.dialogs.PlainTextRuleDialog;

public final class PlainTextRuleDialogBuilder {

    private final BuilderFactory builderFactory;

    private final PlainTextRuleService plainTextRuleService;

    private PlainTextRule rule;

    private EntityEventListener eventListener;

    PlainTextRuleDialogBuilder(BuilderFactory builderFactory, PlainTextRuleService plainTextRuleService) {
        this.builderFactory = builderFactory;
        this.plainTextRuleService = plainTextRuleService;
    }

    public PlainTextRuleDialogBuilder forRule(PlainTextRule rule) {
        this.rule = rule;
        return this;
    }

    public PlainTextRuleDialogBuilder withEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public PlainTextRuleDialog build() {
        PlainTextRuleDialog dialog = rule == null ? new PlainTextRuleDialog(builderFactory, plainTextRuleService) : new PlainTextRuleDialog(builderFactory, rule, plainTextRuleService);
        dialog.setEventListener(eventListener);
        return dialog;
    }
}
