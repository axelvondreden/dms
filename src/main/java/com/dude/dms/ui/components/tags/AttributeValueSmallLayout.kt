package com.dude.dms.ui.components.tags

import com.dude.dms.backend.containers.DocContainer
import com.vaadin.flow.component.html.Div

class AttributeValueSmallLayout : Div() {

    init {
        addClassName("attribute-container")
    }

    fun fill(docContainer: DocContainer) {
        clear()
        docContainer.attributeValues.forEach { add(AttributeValueLabel(it)) }
    }

    fun clear() {
        removeAll()
    }
}