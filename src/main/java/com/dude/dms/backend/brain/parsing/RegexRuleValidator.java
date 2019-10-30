package com.dude.dms.backend.brain.parsing;

import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.RegexRuleService;
import com.dude.dms.backend.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RegexRuleValidator extends RuleValidator<RegexRule> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegexRuleValidator.class);

    private final RegexRuleService regexRuleService;

    @Autowired
    public RegexRuleValidator(RegexRuleService regexRuleService, TagService tagService, DocService docService) {
        super(tagService, docService);
        this.regexRuleService = regexRuleService;
    }

    @Override
    public Set<Tag> getTagsForRule(String rawText, RegexRule rule) {
        Set<Tag> tags = new HashSet<>();
        for (String line : rawText.split("\n")) {
            if (rule.validate(line)) {
                Set<Tag> ruleTags = tagService.findByRegexRule(rule);
                LOGGER.info("{} found a match! Adding tags[{}]...", rule, ruleTags.stream().map(Tag::getName).collect(Collectors.joining(",")));
                tags.addAll(ruleTags);
                break;
            }
        }
        return tags;
    }

    @Override
    public Set<Tag> getTags(String rawText) {
        Set<Tag> tags = new HashSet<>();
        regexRuleService.getActiveRules().stream().map(rule -> getTagsForRule(rawText, rule)).forEach(tags::addAll);
        return tags;
    }
}