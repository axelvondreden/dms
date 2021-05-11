package com.dude.dms.brain.parsing

import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.filter.RegexRule
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

    override fun getTagsForRule(docText: String?, rule: RegexRule) = docText?.let { text ->
        if (text.split("\n").any { rule.validate(it) }) {
            return rule.tags.map { TagContainer(it, t("rule") + ": " + rule.regex) }.toSet()
        }
        emptySet<TagContainer>()
    } ?: emptySet()

    override fun getTags(docText: String?) = docText?.let {
        regexRuleService.findAll().flatMap { getTagsForRule(docText, it) }.toSet()
    } ?: emptySet()
}