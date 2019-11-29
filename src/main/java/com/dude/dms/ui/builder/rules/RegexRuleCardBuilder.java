package com.dude.dms.ui.builder.rules;

import com.dude.dms.backend.brain.parsing.RegexRuleValidator;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityCreateListener;
import com.dude.dms.ui.EntityDeleteListener;
import com.dude.dms.ui.EntityEditListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.cards.RegexRuleCard;

public final class RegexRuleCardBuilder {

    private final BuilderFactory builderFactory;

    private final RegexRule rule;

    private final RegexRuleValidator regexRuleValidator;

    private final TagService tagService;

    private EntityCreateListener<RegexRule> createListener;

    private EntityEditListener<RegexRule> editListener;

    private EntityDeleteListener<RegexRule> deleteListener;

    RegexRuleCardBuilder(BuilderFactory builderFactory, RegexRule rule, RegexRuleValidator regexRuleValidator, TagService tagService) {
        this.builderFactory = builderFactory;
        this.rule = rule;
        this.regexRuleValidator = regexRuleValidator;
        this.tagService = tagService;
    }

    public RegexRuleCardBuilder withCreateListener(EntityCreateListener<RegexRule> createListener) {
        this.createListener = createListener;
        return this;
    }

    public RegexRuleCardBuilder withEditListener(EntityEditListener<RegexRule> editListener) {
        this.editListener = editListener;
        return this;
    }

    public RegexRuleCardBuilder withDeleteListener(EntityDeleteListener<RegexRule> deleteListener) {
        this.deleteListener = deleteListener;
        return this;
    }

    public RegexRuleCard build() {
        RegexRuleCard card = new RegexRuleCard(builderFactory, rule, regexRuleValidator, tagService);
        card.addClickListener(e -> builderFactory.rules().regexDialog().forRule(rule).withCreateListener(createListener)
                .withEditListener(editListener).withDeleteListener(deleteListener).build().open());
        return card;
    }
}
