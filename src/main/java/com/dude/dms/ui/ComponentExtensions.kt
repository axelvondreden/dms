package com.dude.dms.ui

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.TagService
import com.dude.dms.ui.components.tags.TagLabel
import com.dude.dms.ui.components.tags.TagLayout
import com.dude.dms.ui.components.tags.TagSelector
import com.github.mvysny.karibudsl.v10.init
import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.HasComponents

fun HasComponents.tagLayout(
        tagService: TagService,
        tags: MutableSet<Tag> = mutableSetOf(),
        edit: Boolean = false,
        compact: Boolean = false,
        block: TagLayout.() -> Unit = {}
): TagLayout = init(TagLayout(tagService, tags, edit, compact), block)

fun HasComponents.tagLabel(tag: Tag, block: TagLabel.() -> Unit = {}): TagLabel = init(TagLabel(tag), block)

fun HasComponents.tagSelector(tagService: TagService, block: TagSelector.() -> Unit = {}): TagSelector = init(TagSelector(tagService), block)

fun selector(doc: DocContainer? = null, pRule: PlainTextRule? = null, rRule: RegexRule? = null) = TagSelector(tagService).apply {
    selectedTags = doc?.tags ?: pRule?.tags?.map { TagContainer(it) }?.toSet() ?: rRule?.tags?.map { TagContainer(it) }?.toSet() ?: emptySet()
}