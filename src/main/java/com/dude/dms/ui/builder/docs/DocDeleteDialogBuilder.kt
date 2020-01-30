package com.dude.dms.ui.builder.docs

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.MailService
import com.dude.dms.brain.DeleteEvent
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.components.dialogs.DocDeleteDialog

class DocDeleteDialogBuilder(
        private val doc: Doc,
        private val docService: DocService,
        private val mailService: MailService,
        private var deleteEvent: DeleteEvent<Doc>? = null
): Builder<DocDeleteDialog> {

    override fun build() = DocDeleteDialog(doc, docService, mailService).also { it.deleteEvent = deleteEvent }
}