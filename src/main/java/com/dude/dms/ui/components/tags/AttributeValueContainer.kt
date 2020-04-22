package com.dude.dms.ui.components.tags

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.brain.DmsLogger
import com.dude.dms.ui.builder.BuilderFactory
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.html.Div

class AttributeValueContainer(private val builderFactory: BuilderFactory, private val readOnly: Boolean = false) : Div() {

    private val fields = mutableListOf<AttributeValueField>()

    var onChange: (() -> Unit)? = null

    init {
        addClassName("attribute-container")
    }

    fun fill(docContainer: DocContainer) {
        clear()
        docContainer.attributeValues.forEach {
            val field = builderFactory.attributes().valueField(it, readOnly).also { field ->
                field.setWidthFull()
                field.onChange = onChange
            }
            add(field)
            fields.add(field)
        }
    }

    fun clear() {
        removeAll()
        fields.clear()
    }

    fun validate(silent: Boolean = false): Boolean {
        for (field in fields) {
            if (!field.validate()) {
                if (!silent) LOGGER.showError("Attribute '" + field.label + " is required.", UI.getCurrent())
                return false
            }
        }
        return true
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(AttributeValueContainer::class.java)
    }
}