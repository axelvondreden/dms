package com.dude.dms.brain.parsing

import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.PlainTextRuleService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.t
import org.springframework.stereotype.Component

@Component
class PlainTextRuleValidator(
        private val plainTextRuleService: PlainTextRuleService,
        tagService: TagService, docService: DocService
) : RuleValidator<PlainTextRule>(tagService, docService) {

    override fun getTagsForRule(rawText: String?, rule: PlainTextRule) = rawText?.let { text ->
        if (text.split("\n").any { rule.validate(it) }) {
            return tagService.findByPlainTextRule(rule).map { TagContainer(it, t("rule") + ": " + rule.text) }.toSet()
        }
        emptySet<TagContainer>()
    } ?: emptySet()

    override fun getTags(rawText: String?) = rawText?.let {
        plainTextRuleService.findAll().flatMap { getTagsForRule(rawText, it) }.toSet()
    } ?: emptySet()
}