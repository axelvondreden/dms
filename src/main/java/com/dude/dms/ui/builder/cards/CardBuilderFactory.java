package com.dude.dms.ui.builder.cards;

import com.dude.dms.backend.brain.parsing.PlainTextRuleValidator;
import com.dude.dms.backend.brain.parsing.RegexRuleValidator;
import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.builder.Factory;

public class CardBuilderFactory extends Factory {

    private final TagService tagService;

    private final PlainTextRuleValidator plainTextRuleValidator;

    private final RegexRuleValidator regexRuleValidator;

    public CardBuilderFactory(BuilderFactory builderFactory, TagService tagService, PlainTextRuleValidator plainTextRuleValidator, RegexRuleValidator regexRuleValidator) {
        super(builderFactory);
        this.tagService = tagService;
        this.plainTextRuleValidator = plainTextRuleValidator;
        this.regexRuleValidator = regexRuleValidator;
    }

    public PlainTextRuleCardBuilder plainTextRule(PlainTextRule rule) {
        return new PlainTextRuleCardBuilder(builderFactory, rule, plainTextRuleValidator, tagService);
    }

    public RegexRuleCardBuilder regexRule(RegexRule rule) {
        return new RegexRuleCardBuilder(builderFactory, rule, regexRuleValidator, tagService);
    }
}