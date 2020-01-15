package com.dude.dms.ui.builder.docs

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.brain.CreateEvent
import com.dude.dms.backend.brain.polling.PollingService
import com.dude.dms.ui.components.dialogs.DocCreateDialog

class DocCreateDialogBuilder(private val pollingService: PollingService, private var createEvent: CreateEvent<Doc>? = null) {

    fun build() = DocCreateDialog(pollingService).also { it.createEvent = createEvent }
}