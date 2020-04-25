package com.dude.dms.brain.parsing

import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.rules.Rule
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService

abstract class RuleValidator<T : Rule>(protected val tagService: TagService, protected val docService: DocService) {

    abstract fun getTagsForRule(rawText: String?, rule: T): Set<TagContainer>

    abstract fun getTags(rawText: String?): Set<TagContainer>

    fun runRuleForAll(rule: T) = docService.findAll().map { doc ->
        doc to getTagsForRule(doc.rawText, rule).filter { it.tag !in doc.tags }.toSet()
    }.filter { it.second.isNotEmpty() }.toMap()
}