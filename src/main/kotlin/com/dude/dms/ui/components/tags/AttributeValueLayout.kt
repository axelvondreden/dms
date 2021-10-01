package com.dude.dms.ui.components.tags

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.t
import com.dude.dms.utils.attributeValueField
import com.dude.dms.ui.components.misc.DocImageEditor
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.html.Div

class AttributeValueLayout(private val imageEditor: DocImageEditor? = null) : Div() {

    private val fields = mutableSetOf<AttributeValueField>()

    fun fill(docContainer: DocContainer) {
        clear()
        if (docContainer.tags.isNotEmpty()) {
            docContainer.attributeValues.forEach {
                val field = attributeValueField(it, imageEditor)
                fields.add(field)
            }
        }
    }

    fun clear() {
        removeAll()
        fields.clear()
    }

    fun validate(silent: Boolean = false): Boolean {
        for (field in fields) {
            if (!field.validate()) {
                if (!silent) LOGGER.showError(t("attribute.value.missing", field.label), UI.getCurrent())
                return false
            }
        }
        return true
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(AttributeValueLayout::class.java)
    }
}