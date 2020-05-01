package com.dude.dms.ui.builder

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.*
import com.dude.dms.ui.components.dialogs.TagCreateDialog
import com.dude.dms.ui.components.dialogs.TagDeleteDialog
import com.dude.dms.ui.components.dialogs.TagEditDialog
import com.dude.dms.ui.components.tags.TagLayout
import com.dude.dms.ui.components.tags.TagSelector
import com.vaadin.flow.component.ComponentEvent

class TagBuilderFactory(
        builderFactory: BuilderFactory,
        private val tagService: TagService,
        private val docService: DocService,
        private val mailService: MailService,
        private val attributeService: AttributeService,
        private val plainTextRuleService: PlainTextRuleService,
        private val regexRuleService: RegexRuleService,
        private val mailFilterService: MailFilterService
) : Factory(builderFactory) {

    fun createDialog() = TagCreateDialog(builderFactory, tagService)

    fun editDialog(tag: Tag) = TagEditDialog(builderFactory, tag, tagService, docService)

    fun deleteDialog(tag: Tag) = TagDeleteDialog(tag, tagService, docService, mailService, attributeService, plainTextRuleService, regexRuleService, mailFilterService)

    fun selector(doc: DocContainer? = null, pRule: PlainTextRule? = null, rRule: RegexRule? = null) = TagSelector(tagService).apply {
        selectedTags = doc?.tags ?: pRule?.tags?.map { TagContainer(it) }?.toSet() ?: rRule?.tags?.map { TagContainer(it) }?.toSet() ?: emptySet()
    }

    fun container(
            tags: MutableSet<Tag>,
            edit: Boolean = false,
            compact: Boolean = false,
            onClick: ((ComponentEvent<*>) -> Unit)? = null
    ) = TagLayout(builderFactory, tags, tagService, edit, compact, onClick)
}