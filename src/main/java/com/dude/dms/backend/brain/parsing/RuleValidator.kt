package com.dude.dms.backend.brain.parsing

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.rules.Rule
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import java.util.*

abstract class RuleValidator<T : Rule>(protected val tagService: TagService, protected val docService: DocService) {

    abstract fun getTagsForRule(rawText: String?, rule: T): Set<Tag>

    abstract fun getTags(rawText: String?): Set<Tag?>?

    fun runRuleForAll(rule: T): Map<Doc, Set<Tag>> {
        val result: MutableMap<Doc, Set<Tag>> = HashMap()
        val docs = docService.findAll()
        for (doc in docs) {
            val tags = getTagsForRule(doc.rawText, rule).toMutableSet().apply { removeAll(doc.tags) }
            if (tags.isNotEmpty()) {
                result[doc] = tags
            }
        }
        return result
    }
}