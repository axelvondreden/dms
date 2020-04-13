package com.dude.dms.ui.components.tags

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
import com.dude.dms.ui.builder.BuilderFactory
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.html.Div

class AttributeValueContainer(
        private val builderFactory: BuilderFactory,
        eventManager: EventManager,
        private val docContainer: DocContainer,
        private val readOnly: Boolean = false
) : Div() {

    private val fields = mutableListOf<AttributeValueField>()

    init {
        addClassName("attribute-container")

        fill()

        eventManager.register(this, Attribute::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { fill() }
        eventManager.register(this, AttributeValue::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { fill() }
    }

    private fun fill() {
        removeAll()
        fields.clear()
        for (attributeValue in docContainer.attributeValues) {
            val field = builderFactory.attributes().valueField(attributeValue, readOnly).apply { setWidthFull() }
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