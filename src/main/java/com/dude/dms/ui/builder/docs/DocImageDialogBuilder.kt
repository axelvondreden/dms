package com.dude.dms.ui.builder.docs

import com.dude.dms.brain.FileManager
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.TextBlockService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.dialogs.DocImageDialog

class DocImageDialogBuilder(
        private val builderFactory: BuilderFactory,
        private val doc: Doc,
        private val textBlockService: TextBlockService,
        private val fileManager: FileManager
) {

    fun build() = DocImageDialog(builderFactory, doc, textBlockService, fileManager)
}