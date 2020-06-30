package com.dude.dms.ui.builder

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.AttributeValueService
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.ui.components.dialogs.AttributeCreateDialog
import com.dude.dms.ui.components.dialogs.AttributeDeleteDialog
import com.dude.dms.ui.components.misc.DocImageEditor
import com.dude.dms.ui.components.tags.AttributeSelector
import com.dude.dms.ui.components.tags.AttributeValueLayout
import com.dude.dms.ui.components.tags.AttributeValueField

class AttributeBuilderFactory(
        builderFactory: BuilderFactory,
        private val attributeService: AttributeService,
        private val attributeValueService: AttributeValueService,
        private val docService: DocService,
        private val tagService: TagService,
        private val eventManager: EventManager
) : Factory(builderFactory) {

    fun selector(tag: Tag? = null) = AttributeSelector(builderFactory, attributeService, eventManager).also {
        it.selectedAttributes = tag?.attributes ?: emptySet()
    }

    fun valueContainer(imageEditor: DocImageEditor? = null) = AttributeValueLayout(builderFactory, imageEditor)

    fun valueField(attributeValue: AttributeValue, imageEditor: DocImageEditor? = null) = AttributeValueField(attributeValue, attributeValueService, imageEditor)

    fun createDialog() = AttributeCreateDialog(attributeService)

    fun deleteDialog(attribute: Attribute) = AttributeDeleteDialog(attribute, attributeService, docService, tagService)
}