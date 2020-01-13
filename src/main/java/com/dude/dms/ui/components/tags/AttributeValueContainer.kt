package com.dude.dms.ui.components.tags

import com.dude.dms.backend.brain.DmsLogger
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.AttributeValueService
import com.dude.dms.ui.builder.BuilderFactory
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import java.util.*

class AttributeValueContainer(builderFactory: BuilderFactory, doc: Doc, attributeValueService: AttributeValueService) : VerticalLayout() {

    private val fields = ArrayList<AttributeValueField>()

    init {
        isPadding = false
        isSpacing = false

        for (attributeValue in attributeValueService.findByDoc(doc)) {
            val field = builderFactory.attributes().valueField(attributeValue).build().apply { setWidthFull() }
            add(field)
            fields.add(field)
        }
    }

    fun validate(): Boolean {
        for (field in fields) {
            if (!field.validate()) {
                LOGGER.showError("Attribute '" + field.label + " is required.")
                return false
            }
        }
        return true
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(AttributeValueContainer::class.java)
    }
}