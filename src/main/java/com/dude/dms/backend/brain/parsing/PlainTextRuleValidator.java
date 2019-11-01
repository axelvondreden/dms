package com.dude.dms.backend.brain.parsing;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.PlainTextRuleService;
import com.dude.dms.backend.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PlainTextRuleValidator extends RuleValidator<PlainTextRule> {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(PlainTextRuleValidator.class);

    private final PlainTextRuleService plainTextRuleService;

    @Autowired
    public PlainTextRuleValidator(PlainTextRuleService plainTextRuleService, TagService tagService, DocService docService) {
        super(tagService, docService);
        this.plainTextRuleService = plainTextRuleService;
    }

    @Override
    public Set<Tag> getTagsForRule(String rawText, PlainTextRule rule) {
        Set<Tag> tags = new HashSet<>();
        if (rawText != null && !rawText.isEmpty()) {
            for (String line : rawText.split("\n")) {
                if (rule.validate(line)) {
                    Set<Tag> ruleTags = tagService.findByPlainTextRule(rule);
                    LOGGER.info("{} found a match! Adding tags[{}]...", rule, ruleTags.stream().map(Tag::getName).collect(Collectors.joining(",")));
                    tags.addAll(ruleTags);
                    break;
                }
            }
        }
        return tags;
    }

    @Override
    public Set<Tag> getTags(String rawText) {
        Set<Tag> tags = new HashSet<>();
        if (rawText != null && !rawText.isEmpty()) {
            plainTextRuleService.getActiveRules().stream().map(rule -> getTagsForRule(rawText, rule)).forEach(tags::addAll);
        }
        return tags;
    }
}