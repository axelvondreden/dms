package com.dude.dms.ui.builder.docs

import com.dude.dms.brain.FileManager
import com.dude.dms.brain.polling.PollingService
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.TextBlock
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.MailService
import com.dude.dms.backend.service.TagService
import com.dude.dms.backend.service.TextBlockService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.builder.Factory

class DocBuilderFactory(
        builderFactory: BuilderFactory,
        private val docService: DocService,
        private val mailService: MailService,
        private val tagService: TagService,
        private val textBlockService: TextBlockService,
        private val pollingService: PollingService,
        private val fileManager: FileManager
) : Factory(builderFactory) {

    fun searchResult(doc: Doc, search: String) = DocSearchResultBuilder(doc, search, builderFactory.docs().imageDialog(doc).build(), builderFactory.docs().textDialog(doc).build(), tagService)

    fun createDialog() = DocCreateDialogBuilder(pollingService)

    fun editDialog(doc: Doc) = DocEditDialogBuilder(builderFactory, doc, docService)

    fun deleteDialog(doc: Doc) = DocDeleteDialogBuilder(doc, docService, mailService)

    fun imageDialog(doc: Doc) = DocImageDialogBuilder(builderFactory, doc, textBlockService, fileManager)

    fun textDialog(doc: Doc) = DocTextDialogBuilder(doc, textBlockService)

    fun textBlockEditDialog(textBlock: TextBlock) = TextBlockEditDialogBuilder(textBlock, textBlockService, docService)
}