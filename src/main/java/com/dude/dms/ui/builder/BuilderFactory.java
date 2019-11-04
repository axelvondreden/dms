package com.dude.dms.ui.builder;

import com.dude.dms.backend.brain.parsing.PlainTextRuleValidator;
import com.dude.dms.backend.brain.parsing.RegexRuleValidator;
import com.dude.dms.backend.service.*;
import com.dude.dms.ui.builder.attributes.AttributeBuilderFactory;
import com.dude.dms.ui.builder.docs.DocBuilderFactory;
import com.dude.dms.ui.builder.misc.MiscBuilderFactory;
import com.dude.dms.ui.builder.rules.RuleBuilderFactory;
import com.dude.dms.ui.builder.tags.TagBuilderFactory;
import com.dude.dms.updater.UpdateChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuilderFactory {

    private final DocService docService;
    private final TagService tagService;
    private final PlainTextRuleService plainTextRuleService;
    private final RegexRuleService regexRuleService;
    private final ChangelogService changelogService;
    private final UpdateChecker updateChecker;
    private final TextBlockService textBlockService;
    private final PlainTextRuleValidator plainTextRuleValidator;
    private final RegexRuleValidator regexRuleValidator;
    private final AttributeService attributeService;
    private final AttributeValueService attributeValueService;

    @Autowired
    public BuilderFactory(DocService docService, TagService tagService, PlainTextRuleService plainTextRuleService,
                          RegexRuleService regexRuleService, ChangelogService changelogService, UpdateChecker updateChecker,
                          TextBlockService textBlockService, PlainTextRuleValidator plainTextRuleValidator, RegexRuleValidator regexRuleValidator,
                          AttributeService attributeService, AttributeValueService attributeValueService) {
        this.docService = docService;
        this.tagService = tagService;
        this.plainTextRuleService = plainTextRuleService;
        this.regexRuleService = regexRuleService;
        this.changelogService = changelogService;
        this.updateChecker = updateChecker;
        this.textBlockService = textBlockService;
        this.plainTextRuleValidator = plainTextRuleValidator;
        this.regexRuleValidator = regexRuleValidator;
        this.attributeService = attributeService;
        this.attributeValueService = attributeValueService;
    }

    public AttributeBuilderFactory attributes() {
        return new AttributeBuilderFactory(this, attributeService, attributeValueService);
    }

    public DocBuilderFactory docs() {
        return new DocBuilderFactory(this, docService, tagService, textBlockService);
    }

    public MiscBuilderFactory misc() {
        return new MiscBuilderFactory(this, changelogService, updateChecker);
    }

    public RuleBuilderFactory rules() {
        return new RuleBuilderFactory(this, tagService, plainTextRuleValidator, regexRuleValidator, plainTextRuleService, regexRuleService, docService);
    }

    public TagBuilderFactory tags() {
        return new TagBuilderFactory(this, tagService, docService);
    }
}