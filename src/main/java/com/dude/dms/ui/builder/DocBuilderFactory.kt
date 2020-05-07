package com.dude.dms.ui.builder

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.WordContainer
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.LineService
import com.dude.dms.backend.service.MailService
import com.dude.dms.backend.service.WordService
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.ui.components.cards.DocCard
import com.dude.dms.ui.components.dialogs.DocDeleteDialog
import com.dude.dms.ui.components.dialogs.DocImageDialog
import com.dude.dms.ui.components.dialogs.WordEditDialog
import com.dude.dms.ui.components.misc.DocImageEditor
import com.dude.dms.ui.components.misc.DocImportPreview
import com.dude.dms.ui.components.misc.DocInfoLayout

class DocBuilderFactory(
        builderFactory: BuilderFactory,
        private val docParser: DocParser,
        private val docService: DocService,
        private val fileManager: FileManager,
        private val mailService: MailService,
        private val lineService: LineService,
        private val wordService: WordService
) : Factory(builderFactory) {

    fun deleteDialog(docContainer: DocContainer) = DocDeleteDialog(docContainer, docService, mailService)

    fun imageDialog() = DocImageDialog(builderFactory, docService)

    fun wordEditDialog(wordContainer: WordContainer) = WordEditDialog(wordService, wordContainer)

    fun card(docContainer: DocContainer, imageDialog: DocImageDialog) = DocCard(builderFactory, docService, docContainer, imageDialog)

    fun imageEditor() = DocImageEditor(builderFactory, lineService, wordService, docParser, fileManager)

    fun importPreview() = DocImportPreview(builderFactory)

    fun infoLayout(imageEditor: DocImageEditor) = DocInfoLayout(builderFactory, imageEditor)
}