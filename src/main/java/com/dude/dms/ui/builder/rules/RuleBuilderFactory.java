package com.dude.dms.ui.builder.rules;

import com.dude.dms.backend.brain.parsing.PlainTextRuleValidator;
import com.dude.dms.backend.brain.parsing.RegexRuleValidator;
import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.PlainTextRuleService;
import com.dude.dms.backend.service.RegexRuleService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.builder.Factory;

import java.util.Map;
import java.util.Set;

public class RuleBuilderFactory extends Factory {

    private final TagService tagService;
    private final PlainTextRuleValidator plainTextRuleValidator;
    private final RegexRuleValidator regexRuleValidator;
    private final PlainTextRuleService plainTextRuleService;
    private final RegexRuleService regexRuleService;
    private final DocService docService;

    public RuleBuilderFactory(BuilderFactory builderFactory, TagService tagService, PlainTextRuleValidator plainTextRuleValidator,
                              RegexRuleValidator regexRuleValidator, PlainTextRuleService plainTextRuleService, RegexRuleService regexRuleService,
                              DocService docService) {
        super(builderFactory);
        this.tagService = tagService;
        this.plainTextRuleValidator = plainTextRuleValidator;
        this.regexRuleValidator = regexRuleValidator;
        this.plainTextRuleService = plainTextRuleService;
        this.regexRuleService = regexRuleService;
        this.docService = docService;
    }

    public PlainTextRuleCardBuilder plainTextCard(PlainTextRule rule) {
        return new PlainTextRuleCardBuilder(builderFactory, rule, plainTextRuleValidator, tagService);
    }

    public RegexRuleCardBuilder regexCard(RegexRule rule) {
        return new RegexRuleCardBuilder(builderFactory, rule, regexRuleValidator, tagService);
    }

    public PlainTextRuleDialogBuilder plainDialog() {
        return new PlainTextRuleDialogBuilder(tagService, plainTextRuleService);
    }

    public RegexRuleDialogBuilder regexDialog() {
        return new RegexRuleDialogBuilder(tagService, regexRuleService);
    }

    public RuleRunnerDialogBuilder ruleRunnerDialog(Map<Doc, Set<Tag>> result) {
        return new RuleRunnerDialogBuilder(result, docService);
    }
}