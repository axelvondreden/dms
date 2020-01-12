package com.dude.dms.backend.brain.parsing

import com.dude.dms.backend.brain.DmsLogger
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.PlainTextRuleService
import com.dude.dms.backend.service.TagService
import org.springframework.stereotype.Component
import java.util.*

@Component
class PlainTextRuleValidator(
        private val plainTextRuleService: PlainTextRuleService,
        tagService: TagService, docService: DocService
) : RuleValidator<PlainTextRule>(tagService, docService) {

    override fun getTagsForRule(rawText: String?, rule: PlainTextRule): Set<Tag> {
        val tags = HashSet<Tag>()
        if (rawText != null && rawText.isNotEmpty()) {
            for (line in rawText.split("\n").toTypedArray()) {
                if (rule.validate(line)) {
                    val ruleTags = tagService.findByPlainTextRule(rule)
                    LOGGER.info("{} found a match! Adding tags[{}]...", rule, ruleTags.joinToString(",") { it.name })
                    tags += ruleTags
                    break
                }
            }
        }
        return tags
    }

    override fun getTags(rawText: String?): Set<Tag> {
        val tags: MutableSet<Tag> = HashSet()
        if (rawText != null && rawText.isNotEmpty()) {
            plainTextRuleService.findAll().map { getTagsForRule(rawText, it) }.forEach { tags += it }
        }
        return tags
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(PlainTextRuleValidator::class.java)
    }
}