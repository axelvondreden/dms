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
import com.dude.dms.ui.components.dialogs.DocEditDialog
import com.dude.dms.ui.components.dialogs.DocImageDialog
import com.dude.dms.ui.components.dialogs.WordEditDialog
import com.dude.dms.ui.components.misc.DocImageEditor
import com.dude.dms.ui.components.misc.DocImportPreview

class DocBuilderFactory(
        builderFactory: BuilderFactory,
        private val docParser: DocParser,
        private val docService: DocService,
        private val fileManager: FileManager,
        private val mailService: MailService,
        private val lineService: LineService,
        private val wordService: WordService
) : Factory(builderFactory) {

    fun editDialog(docContainer: DocContainer) = DocEditDialog(builderFactory, docContainer, docService)

    fun deleteDialog(docContainer: DocContainer) = DocDeleteDialog(docContainer, docService, mailService)

    fun imageDialog(docContainer: DocContainer) = DocImageDialog(builderFactory, docContainer, docService)

    fun wordEditDialog(wordContainer: WordContainer) = WordEditDialog(wordService, wordContainer)

    fun card(docContainer: DocContainer) = DocCard(builderFactory, fileManager, docContainer)

    fun imageEditor() = DocImageEditor(builderFactory, lineService, wordService, docParser, fileManager)

    fun importPreview() = DocImportPreview(builderFactory, docService)
}