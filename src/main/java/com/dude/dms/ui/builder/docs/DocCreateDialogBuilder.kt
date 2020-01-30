package com.dude.dms.ui.builder.docs

import com.dude.dms.brain.CreateEvent
import com.dude.dms.brain.polling.PollingService
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.components.dialogs.DocCreateDialog

class DocCreateDialogBuilder(
        private val pollingService: PollingService,
        private var createEvent: CreateEvent<Doc>? = null
): Builder<DocCreateDialog> {

    override fun build() = DocCreateDialog(pollingService).also { it.createEvent = createEvent }
}