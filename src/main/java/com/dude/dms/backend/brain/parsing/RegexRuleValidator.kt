package com.dude.dms.backend.brain.parsing

import com.dude.dms.backend.brain.DmsLogger
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.RegexRuleService
import com.dude.dms.backend.service.TagService
import org.springframework.stereotype.Component
import java.util.*

@Component
class RegexRuleValidator(
        private val regexRuleService: RegexRuleService,
        tagService: TagService,
        docService: DocService
) : RuleValidator<RegexRule>(tagService, docService) {

    override fun getTagsForRule(rawText: String?, rule: RegexRule): Set<Tag> {
        val tags = HashSet<Tag>()
        if (rawText != null) {
            for (line in rawText.split("\n").toTypedArray()) {
                if (rule.validate(line)) {
                    val ruleTags = tagService.findByRegexRule(rule)
                    LOGGER.info("{} found a match! Adding tags[{}]...", rule, ruleTags.joinToString(",") { it.name })
                    tags += ruleTags
                    break
                }
            }
        }
        return tags
    }

    override fun getTags(rawText: String?): Set<Tag> {
        val tags = HashSet<Tag>()
        regexRuleService.findAll().map { getTagsForRule(rawText, it) }.forEach { tags += it }
        return tags
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(RegexRuleValidator::class.java)
    }
}