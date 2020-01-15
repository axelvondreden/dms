package com.dude.dms.ui.builder.attributes

import com.dude.dms.backend.brain.CreateEvent
import com.dude.dms.backend.brain.EditEvent
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.AttributeValueService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.builder.Factory

class AttributeBuilderFactory(
        builderFactory: BuilderFactory,
        private val attributeService: AttributeService,
        private val attributeValueService: AttributeValueService
) : Factory(builderFactory) {

    fun selector() = AttributeSelectorBuilder(builderFactory, attributeService)

    fun valueContainer(doc: Doc) = AttributeValueContainerBuilder(builderFactory, doc, attributeValueService)

    fun valueField(attributeValue: AttributeValue) = AttributeValueFieldBuilder(attributeValue, attributeValueService)

    fun createDialog(createEvent: CreateEvent<Attribute>? = null) = AttributeCreateDialogBuilder(attributeService, createEvent)

    fun editDialog(attribute: Attribute, editEvent: EditEvent<Attribute>? = null) = AttributeEditDialogBuilder(attribute, attributeService, editEvent)
}