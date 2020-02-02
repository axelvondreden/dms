package com.dude.dms.ui.builder.attributes

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.AttributeValueService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.builder.Factory

class AttributeBuilderFactory(
        builderFactory: BuilderFactory,
        private val attributeService: AttributeService,
        private val attributeValueService: AttributeValueService,
        private val eventManager: EventManager
) : Factory(builderFactory) {

    fun selector() = AttributeSelectorBuilder(builderFactory, attributeService, eventManager)

    fun valueContainer(doc: Doc) = AttributeValueContainerBuilder(builderFactory, doc, attributeValueService, eventManager)

    fun valueField(attributeValue: AttributeValue) = AttributeValueFieldBuilder(attributeValue, attributeService, attributeValueService)

    fun createDialog() = AttributeCreateDialogBuilder(attributeService)

    fun editDialog(attribute: Attribute) = AttributeEditDialogBuilder(attribute, attributeService)
}