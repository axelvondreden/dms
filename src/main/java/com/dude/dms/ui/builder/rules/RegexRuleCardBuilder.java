package com.dude.dms.ui.builder.rules;

import com.dude.dms.backend.brain.parsing.RegexRuleValidator;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityEventListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.cards.RegexRuleCard;

public final class RegexRuleCardBuilder {

    private final BuilderFactory builderFactory;

    private final RegexRule rule;

    private final RegexRuleValidator regexRuleValidator;

    private final TagService tagService;

    private EntityEventListener eventListener;

    RegexRuleCardBuilder(BuilderFactory builderFactory, RegexRule rule, RegexRuleValidator regexRuleValidator, TagService tagService) {
        this.builderFactory = builderFactory;
        this.rule = rule;
        this.regexRuleValidator = regexRuleValidator;
        this.tagService = tagService;
    }

    public RegexRuleCardBuilder withDialogEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public RegexRuleCard build() {
        RegexRuleCard card = new RegexRuleCard(builderFactory, rule, regexRuleValidator, tagService);
        card.addClickListener(e -> builderFactory.rules().regexDialog().forRule(rule).withEventListener(eventListener).build().open());
        return card;
    }
}
