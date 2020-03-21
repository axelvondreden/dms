package com.dude.dms.ui.builder

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.data.docs.Word
import com.dude.dms.backend.service.*
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.brain.polling.DocPollingService
import com.dude.dms.ui.components.cards.DocTagImportCard
import com.dude.dms.ui.components.cards.DocTextImportCard
import com.dude.dms.ui.components.dialogs.DocDeleteDialog
import com.dude.dms.ui.components.dialogs.DocEditDialog
import com.dude.dms.ui.components.dialogs.DocImageDialog
import com.dude.dms.ui.components.dialogs.WordEditDialog
import com.dude.dms.ui.components.dialogs.docimport.DocImportDialog
import com.dude.dms.ui.components.search.DocSearchResult

class DocBuilderFactory(
        builderFactory: BuilderFactory,
        private val docParser: DocParser,
        private val docPollingService: DocPollingService,
        private val docService: DocService,
        private val fileManager: FileManager,
        private val lineService: LineService,
        private val mailService: MailService,
        private val tagService: TagService,
        private val wordService: WordService
) : Factory(builderFactory) {

    fun searchResult(doc: Doc, search: String)
            = DocSearchResult(builderFactory, doc, search, builderFactory.docs().imageDialog(doc), tagService)

    fun editDialog(doc: Doc) = DocEditDialog(builderFactory, doc, docService)

    fun deleteDialog(doc: Doc) = DocDeleteDialog(doc, docService, mailService)

    fun imageDialog(doc: Doc? = null, guid: String? = null, lines: Set<Line> = emptySet())
            = DocImageDialog(builderFactory, lineService, wordService, fileManager, doc, guid, lines)

    fun wordEditDialog(word: Word, doc: Doc? = null, lines: Set<Line> = emptySet()) = WordEditDialog(wordService, word, doc, lines)

    fun importDialog() = DocImportDialog(builderFactory, docService, lineService, wordService, docPollingService, fileManager, docParser)

    fun textImportCard(fileContainer: DocImportDialog.FileContainer) = DocTextImportCard(builderFactory, fileContainer)

    fun tagImportCard(fileContainer: DocImportDialog.FileContainer) = DocTagImportCard(builderFactory, tagService, fileContainer)
}