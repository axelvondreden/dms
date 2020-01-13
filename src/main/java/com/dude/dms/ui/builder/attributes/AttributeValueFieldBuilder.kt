package com.dude.dms.ui.builder.attributes

import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.service.AttributeValueService
import com.dude.dms.ui.components.tags.AttributeValueField

class AttributeValueFieldBuilder(private val attributeValue: AttributeValue, private val attributeValueService: AttributeValueService) {

    fun build() = AttributeValueField(attributeValue, attributeValueService)
}