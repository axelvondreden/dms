package com.dude.dms.ui.builder

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.TagService
import com.dude.dms.ui.components.tags.TagSelector

class TagSelectorBuilder(
        private val tagService: TagService,
        private var selected: Set<Tag> = HashSet(),
        private var rawText: String? = null
): Builder<TagSelector> {

    fun forDoc(doc: Doc) = apply {
        selected = tagService.findByDoc(doc)
        rawText = doc.rawText
    }

    fun forRule(rule: PlainTextRule) = apply { selected = tagService.findByPlainTextRule(rule) }

    fun forRule(rule: RegexRule) = apply { selected = tagService.findByRegexRule(rule) }

    override fun build() = TagSelector(tagService).apply {
        selectedTags = selected
        if (rawText != null) {
            setContainedTags(rawText)
        }
    }
}