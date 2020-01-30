package com.dude.dms.ui.builder.tags

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.DocService
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.components.dialogs.TagEditDialog
import com.dude.dms.ui.components.search.TagSearchResult

class TagSearchResultBuilder(
        private val tag: Tag,
        private val tagEditDialog: TagEditDialog,
        private val docService: DocService
): Builder<TagSearchResult> {

    override fun build() = TagSearchResult(tag, docService.countByTag(tag), tagEditDialog)
}