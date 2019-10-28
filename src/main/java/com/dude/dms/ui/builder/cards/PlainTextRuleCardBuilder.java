package com.dude.dms.ui.builder.cards;

import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.service.PlainTextRuleService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityEventListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.cards.PlainTextRuleCard;
import com.dude.dms.ui.components.tags.TagContainer;

public final class PlainTextRuleCardBuilder {

    private final BuilderFactory builderFactory;

    private final TagService tagService;

    private final PlainTextRule rule;

    private String title;

    private EntityEventListener eventListener;

    PlainTextRuleCardBuilder(BuilderFactory builderFactory, PlainTextRule rule, TagService tagService) {
        this.builderFactory = builderFactory;
        this.tagService = tagService;
        this.rule = rule;
        title = rule.getText();
    }

    public PlainTextRuleCardBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public PlainTextRuleCardBuilder withDialogEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public PlainTextRuleCard build() {
        PlainTextRuleCard card = new PlainTextRuleCard(title, new TagContainer(tagService.findByPlainTextRule(rule)));
        card.addClickListener(e -> builderFactory.dialogs().plainTextRule().forRule(rule).withEventListener(eventListener).build().open());
        return card;
    }
}
