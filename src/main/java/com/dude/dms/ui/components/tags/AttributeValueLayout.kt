package com.dude.dms.ui.components.tags

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.brain.DmsLogger
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.misc.DocImageEditor
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.html.Div

class AttributeValueLayout(private val builderFactory: BuilderFactory, private val imageEditor: DocImageEditor? = null) : Div() {

    private val fields = mutableListOf<AttributeValueField>()

    fun fill(docContainer: DocContainer) {
        clear()
        docContainer.attributeValues.forEach {
            val field = builderFactory.attributes().valueField(it, imageEditor).apply { setWidthFull() }
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
        private val LOGGER = DmsLogger.getLogger(AttributeValueLayout::class.java)
    }
}