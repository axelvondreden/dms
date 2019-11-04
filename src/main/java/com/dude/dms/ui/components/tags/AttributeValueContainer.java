package com.dude.dms.ui.components.tags;

import com.dude.dms.backend.data.docs.AttributeValue;
import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.ui.builder.BuilderFactory;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class AttributeValueContainer extends VerticalLayout {

    public AttributeValueContainer(BuilderFactory builderFactory, Doc doc) {
        setPadding(false);
        setSpacing(false);
        for (AttributeValue attributeValue : doc.getAttributeValues()) {
            AttributeValueField field = builderFactory.attributes().valueField(attributeValue).build();
            field.setWidthFull();
            add(field);
        }
    }
}