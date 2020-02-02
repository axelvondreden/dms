package com.dude.dms.ui.builder.tags

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.*
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.components.dialogs.TagDeleteDialog

class TagDeleteDialogBuilder(
        private val tag: Tag,
        private val tagService: TagService,
        private val docService: DocService,
        private val mailService: MailService,
        private val attributeService: AttributeService,
        private val plainTextRuleService: PlainTextRuleService,
        private val regexRuleService: RegexRuleService,
        private val mailFilterService: MailFilterService
): Builder<TagDeleteDialog> {

    override fun build() = TagDeleteDialog(tag, tagService, docService, mailService, attributeService, plainTextRuleService, regexRuleService, mailFilterService)
}