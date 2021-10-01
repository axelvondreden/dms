package com.dude.dms.ui.components.tags

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.utils.attributeValueLabel
import com.vaadin.flow.component.html.Div

class AttributeValueSmallLayout : Div() {

    init {
        addClassName("attribute-container")
    }

    fun fill(docContainer: DocContainer) {
        clear()
        if (docContainer.tags.isNotEmpty()) {
            docContainer.attributeValues.forEach {
                attributeValueLabel(it)
            }
        }
    }

    fun clear() {
        removeAll()
    }
}