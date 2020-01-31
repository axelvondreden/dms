package com.dude.dms.ui.builder.docs

import com.dude.dms.brain.polling.PollingService
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.components.dialogs.DocCreateDialog

class DocCreateDialogBuilder(private val pollingService: PollingService): Builder<DocCreateDialog> {

    override fun build() = DocCreateDialog(pollingService)
}