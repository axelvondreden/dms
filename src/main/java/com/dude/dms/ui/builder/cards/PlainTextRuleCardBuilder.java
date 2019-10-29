package com.dude.dms.ui.builder.cards;

import com.dude.dms.backend.brain.parsing.PlainTextRuleValidator;
import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityEventListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.cards.PlainTextRuleCard;

public final class PlainTextRuleCardBuilder {

    private final BuilderFactory builderFactory;

    private final PlainTextRule rule;

    private final PlainTextRuleValidator plainTextRuleValidator;

    private final TagService tagService;

    private EntityEventListener eventListener;

    PlainTextRuleCardBuilder(BuilderFactory builderFactory, PlainTextRule rule, PlainTextRuleValidator plainTextRuleValidator, TagService tagService) {
        this.builderFactory = builderFactory;
        this.rule = rule;
        this.plainTextRuleValidator = plainTextRuleValidator;
        this.tagService = tagService;
    }

    public PlainTextRuleCardBuilder withDialogEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public PlainTextRuleCard build() {
        PlainTextRuleCard card = new PlainTextRuleCard(builderFactory, rule, plainTextRuleValidator, tagService);
        card.addClickListener(e -> builderFactory.dialogs().plainTextRule().forRule(rule).withEventListener(eventListener).build().open());
        return card;
    }
}
