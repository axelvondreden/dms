package com.dude.dms.ui.builder.docs

import com.dude.dms.brain.FileManager
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.LineService
import com.dude.dms.backend.service.WordService
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.dialogs.DocImageDialog

class DocImageDialogBuilder(
        private val builderFactory: BuilderFactory,
        private val doc: Doc,
        private val lineService: LineService,
        private val wordService: WordService,
        private val fileManager: FileManager
): Builder<DocImageDialog> {

    override fun build() = DocImageDialog(builderFactory, doc, lineService, wordService, fileManager)
}