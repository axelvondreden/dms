package com.dude.dms.ui.components.tags

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.extensions.convert
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class AttributeValueLabel(attributeValue: AttributeValue) : HorizontalLayout() {

    init {
        add(Label("${attributeValue.attribute.name}: ${attributeValue.getValue()}"))
        setWidthFull()
    }

    private fun AttributeValue.getValue() = when (attribute.type) {
        Attribute.Type.STRING -> stringValue
        Attribute.Type.INT -> intValue.toString()
        Attribute.Type.FLOAT -> floatValue.toString()
        Attribute.Type.DATE -> dateValue?.convert()
    }
}