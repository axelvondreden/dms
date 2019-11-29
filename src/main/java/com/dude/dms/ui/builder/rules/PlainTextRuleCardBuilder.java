package com.dude.dms.ui.builder.rules;

import com.dude.dms.backend.brain.parsing.PlainTextRuleValidator;
import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityCreateListener;
import com.dude.dms.ui.EntityDeleteListener;
import com.dude.dms.ui.EntityEditListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.cards.PlainTextRuleCard;

public final class PlainTextRuleCardBuilder {

    private final BuilderFactory builderFactory;

    private final PlainTextRule rule;

    private final PlainTextRuleValidator plainTextRuleValidator;

    private final TagService tagService;

    private EntityCreateListener<PlainTextRule> createListener;

    private EntityEditListener<PlainTextRule> editListener;

    private EntityDeleteListener<PlainTextRule> deleteListener;

    PlainTextRuleCardBuilder(BuilderFactory builderFactory, PlainTextRule rule, PlainTextRuleValidator plainTextRuleValidator, TagService tagService) {
        this.builderFactory = builderFactory;
        this.rule = rule;
        this.plainTextRuleValidator = plainTextRuleValidator;
        this.tagService = tagService;
    }

    public PlainTextRuleCardBuilder withCreateListener(EntityCreateListener<PlainTextRule> createListener) {
        this.createListener = createListener;
        return this;
    }

    public PlainTextRuleCardBuilder withEditListener(EntityEditListener<PlainTextRule> editListener) {
        this.editListener = editListener;
        return this;
}

    public PlainTextRuleCardBuilder withDeleteListener(EntityDeleteListener<PlainTextRule> deleteListener) {
        this.deleteListener = deleteListener;
        return this;
    }

    public PlainTextRuleCard build() {
        PlainTextRuleCard card = new PlainTextRuleCard(builderFactory, rule, plainTextRuleValidator, tagService);
        card.addClickListener(e -> builderFactory.rules().plainDialog().forRule(rule)
                .withCreateListener(createListener).withEditListener(editListener).withDeleteListener(deleteListener).build().open());
        return card;
    }
}
