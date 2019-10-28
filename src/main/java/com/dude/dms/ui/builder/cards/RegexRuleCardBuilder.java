package com.dude.dms.ui.builder.cards;

import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityEventListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.cards.RegexRuleCard;
import com.dude.dms.ui.components.tags.TagContainer;

public final class RegexRuleCardBuilder {

    private final BuilderFactory builderFactory;

    private final TagService tagService;

    private final RegexRule rule;

    private String title;

    private EntityEventListener eventListener;

    RegexRuleCardBuilder(BuilderFactory builderFactory, RegexRule rule, TagService tagService) {
        this.builderFactory = builderFactory;
        this.tagService = tagService;
        this.rule = rule;
        title = rule.getRegex();
    }

    public RegexRuleCardBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public RegexRuleCardBuilder withDialogEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public RegexRuleCard build() {
        RegexRuleCard card = new RegexRuleCard(title, new TagContainer(tagService.findByRegexRule(rule)));
        card.addClickListener(e -> builderFactory.dialogs().regexRule().forRule(rule).withEventListener(eventListener).build().open());
        return card;
    }
}
