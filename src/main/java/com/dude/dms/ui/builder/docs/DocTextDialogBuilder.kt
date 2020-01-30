package com.dude.dms.ui.builder.docs

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.TextBlockService
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.components.dialogs.DocTextDialog

class DocTextDialogBuilder(private val doc: Doc, private val textBlockService: TextBlockService): Builder<DocTextDialog> {

    override fun build() = DocTextDialog(doc, textBlockService)
}