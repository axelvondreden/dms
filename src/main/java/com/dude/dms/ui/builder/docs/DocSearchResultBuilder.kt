package com.dude.dms.ui.builder.docs

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.TagService
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.components.dialogs.DocImageDialog
import com.dude.dms.ui.components.dialogs.DocTextDialog
import com.dude.dms.ui.components.search.DocSearchResult

class DocSearchResultBuilder(
        private val doc: Doc,
        private val search: String,
        private val docImageDialog: DocImageDialog,
        private val docTextDialog: DocTextDialog,
        private val tagService: TagService
): Builder<DocSearchResult> {

    override fun build() = DocSearchResult(doc, search, docImageDialog, docTextDialog, tagService)
}