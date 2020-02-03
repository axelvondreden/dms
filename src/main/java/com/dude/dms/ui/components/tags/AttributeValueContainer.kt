package com.dude.dms.ui.components.tags

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.AttributeValueService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
import com.dude.dms.ui.builder.BuilderFactory
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import java.util.*

class AttributeValueContainer(
        private val builderFactory: BuilderFactory,
        doc: Doc,
        private val attributeValueService: AttributeValueService,
        eventManager: EventManager
) : VerticalLayout() {

    private val fields = ArrayList<AttributeValueField>()

    init {
        isPadding = false
        isSpacing = false

        fill(doc)

        eventManager.register(this, Attribute::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { fill(doc) }
        eventManager.register(this, AttributeValue::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { fill(doc) }
    }

    private fun fill(doc: Doc) {
        removeAll()
        fields.clear()
        for (attributeValue in attributeValueService.findByDoc(doc)) {
            val field = builderFactory.attributes().valueField(attributeValue).build().apply { setWidthFull() }
            add(field)
            fields.add(field)
        }
    }

    fun validate(): Boolean {
        for (field in fields) {
            if (!field.validate()) {
                LOGGER.showError("Attribute '" + field.label + " is required.", UI.getCurrent())
                return false
            }
        }
        return true
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(AttributeValueContainer::class.java)
    }
}