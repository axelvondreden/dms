package com.dude.dms.ui.builder.attributes

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.brain.CreateEvent
import com.dude.dms.ui.components.dialogs.AttributeCreateDialog

class AttributeCreateDialogBuilder(
        private val attributeService: AttributeService,
        private var createEvent: CreateEvent<Attribute>? = null) {

    fun build() = AttributeCreateDialog(attributeService).also { it.createEvent = createEvent }
}