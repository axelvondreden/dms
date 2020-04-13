package com.dude.dms.ui.builder

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.WordContainer
import com.dude.dms.backend.service.*
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.brain.polling.DocImportService
import com.dude.dms.ui.components.cards.DocCard
import com.dude.dms.ui.components.cards.DocTagImportCard
import com.dude.dms.ui.components.cards.DocTextImportCard
import com.dude.dms.ui.components.dialogs.DocDeleteDialog
import com.dude.dms.ui.components.dialogs.DocEditDialog
import com.dude.dms.ui.components.dialogs.DocImageDialog
import com.dude.dms.ui.components.dialogs.WordEditDialog
import com.dude.dms.ui.components.dialogs.docimport.DocImportDialog

class DocBuilderFactory(
        builderFactory: BuilderFactory,
        private val docParser: DocParser,
        private val docImportService: DocImportService,
        private val docService: DocService,
        private val fileManager: FileManager,
        private val lineService: LineService,
        private val mailService: MailService,
        private val tagService: TagService,
        private val wordService: WordService
) : Factory(builderFactory) {

    fun editDialog(docContainer: DocContainer) = DocEditDialog(builderFactory, docContainer, docService)

    fun deleteDialog(docContainer: DocContainer) = DocDeleteDialog(docContainer, docService, mailService)

    fun imageDialog(docContainer: DocContainer) = DocImageDialog(builderFactory, wordService, fileManager, docParser, docContainer)

    fun wordEditDialog(wordContainer: WordContainer) = WordEditDialog(wordService, wordContainer)

    fun importDialog() = DocImportDialog(builderFactory, docService, lineService, wordService, docImportService, fileManager, docParser)

    fun textImportCard(docContainer: DocContainer) = DocTextImportCard(builderFactory, docContainer)

    fun tagImportCard(docContainer: DocContainer) = DocTagImportCard(builderFactory, tagService, docContainer)

    fun card(docContainer: DocContainer) = DocCard(builderFactory, fileManager, docContainer)
}