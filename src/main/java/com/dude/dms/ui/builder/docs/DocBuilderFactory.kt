package com.dude.dms.ui.builder.docs

import com.dude.dms.backend.brain.EditEvent
import com.dude.dms.backend.brain.polling.PollingService
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.TextBlock
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TextBlockService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.builder.Factory

class DocBuilderFactory(
        builderFactory: BuilderFactory,
        private val docService: DocService,
        private val textBlockService: TextBlockService,
        private val pollingService: PollingService
) : Factory(builderFactory) {

    fun searchResult(doc: Doc, search: String) = DocSearchResultBuilder(doc, search, builderFactory.docs().imageDialog(doc).build(), builderFactory.docs().textDialog(doc).build())

    fun createDialog() = DocCreateDialogBuilder(pollingService)

    fun editDialog(doc: Doc, editEvent: EditEvent<Doc>? = null) = DocEditDialogBuilder(builderFactory, doc, docService, editEvent)

    fun imageDialog(doc: Doc) = DocImageDialogBuilder(builderFactory, doc, textBlockService)

    fun textDialog(doc: Doc) = DocTextDialogBuilder(doc, textBlockService)

    fun textBlockEditDialog(textBlock: TextBlock) = TextBlockEditDialogBuilder(textBlock, textBlockService)
}