package com.dude.dms.ui.builder.docs

import com.dude.dms.brain.EditEvent
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.DocService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.dialogs.DocEditDialog

class DocEditDialogBuilder(
        private val builderFactory: BuilderFactory,
        private val doc: Doc,
        private val docService: DocService,
        private var editEvent: EditEvent<Doc>? = null) {

    fun build() = DocEditDialog(builderFactory, doc, docService).also { it.editEvent = editEvent }
}