package com.dude.dms.ui.builder.attributes

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.components.dialogs.AttributeEditDialog

class AttributeEditDialogBuilder(
        private val attribute: Attribute,
        private val attributeService: AttributeService
): Builder<AttributeEditDialog> {

    override fun build() = AttributeEditDialog(attribute, attributeService)
}