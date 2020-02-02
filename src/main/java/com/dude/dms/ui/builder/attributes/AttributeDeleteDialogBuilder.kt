package com.dude.dms.ui.builder.attributes

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.components.dialogs.AttributeDeleteDialog

class AttributeDeleteDialogBuilder(
        private val attribute: Attribute,
        private val attributeService: AttributeService,
        private val docService: DocService,
        private val tagService: TagService
): Builder<AttributeDeleteDialog> {

    override fun build() = AttributeDeleteDialog(attribute, attributeService, docService, tagService)
}