package com.dude.dms.brain.parsing

import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.RegexRuleService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.t
import org.springframework.stereotype.Component

@Component
class RegexRuleValidator(
        private val regexRuleService: RegexRuleService,
        tagService: TagService,
        docService: DocService
) : RuleValidator<RegexRule>(tagService, docService) {

    override fun getTagsForRule(rawText: String?, rule: RegexRule) = rawText?.let { text ->
        if (text.split("\n").any { rule.validate(it) }) {
            return tagService.findByRegexRule(rule).map { TagContainer(it, t("rule") + ": " + rule.regex) }.toSet()
        }
        emptySet<TagContainer>()
    } ?: emptySet()

    override fun getTags(rawText: String?) = rawText?.let {
        regexRuleService.findAll().flatMap { getTagsForRule(rawText, it) }.toSet()
    } ?: emptySet()
}