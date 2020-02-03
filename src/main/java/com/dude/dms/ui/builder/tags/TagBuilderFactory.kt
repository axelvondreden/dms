package com.dude.dms.ui.builder.tags

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.*
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.builder.Factory

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

    fun searchResult(tag: Tag) = TagSearchResultBuilder(tag, builderFactory.tags().editDialog(tag).build(), docService)

    fun createDialog() = TagCreateDialogBuilder(builderFactory, tagService)

    fun editDialog(tag: Tag) = TagEditDialogBuilder(builderFactory, tag, tagService, docService)

    fun deleteDialog(tag: Tag) = TagDeleteDialogBuilder(tag, tagService, docService, mailService, attributeService, plainTextRuleService, regexRuleService, mailFilterService)

    fun selector() = TagSelectorBuilder(tagService)
}