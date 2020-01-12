package com.dude.dms.ui.builder.docs

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.brain.CreateEvent
import com.dude.dms.ui.components.dialogs.DocCreateDialog

class DocCreateDialogBuilder(private var createEvent: CreateEvent<Doc>? = null) {

    fun build() = DocCreateDialog().also { it.createEvent = createEvent }
}