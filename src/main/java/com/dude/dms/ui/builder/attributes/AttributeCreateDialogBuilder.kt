package com.dude.dms.ui.builder.attributes

import com.dude.dms.backend.service.AttributeService
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.components.dialogs.AttributeCreateDialog

class AttributeCreateDialogBuilder(private val attributeService: AttributeService): Builder<AttributeCreateDialog> {

    override fun build() = AttributeCreateDialog(attributeService)
}