package com.dude.dms.ui.builder.cards;

import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.builder.Factory;

public class CardBuilderFactory extends Factory {

    private final TagService tagService;

    public CardBuilderFactory(BuilderFactory builderFactory, TagService tagService) {
        super(builderFactory);
        this.tagService = tagService;
    }

    public PlainTextRuleCardBuilder plainTextRule(PlainTextRule rule) {
        return new PlainTextRuleCardBuilder(builderFactory, rule, tagService);
    }

    public RegexRuleCardBuilder regexRule(RegexRule rule) {
        return new RegexRuleCardBuilder(builderFactory, rule, tagService);
    }
}