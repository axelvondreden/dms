package com.dude.dms.ui.builder.rules;

import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.RegexRuleService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityEventListener;
import com.dude.dms.ui.components.dialogs.RegexRuleDialog;

public final class RegexRuleDialogBuilder {

    private final TagService tagService;
    private final RegexRuleService regexRuleService;

    private RegexRule rule;

    private EntityEventListener eventListener;

    RegexRuleDialogBuilder(TagService tagService, RegexRuleService regexRuleService) {
        this.tagService = tagService;
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
        RegexRuleDialog dialog = rule == null ? new RegexRuleDialog(tagService, regexRuleService) : new RegexRuleDialog(rule, tagService, regexRuleService);
        dialog.setEventListener(eventListener);
        return dialog;
    }
}
