package com.dude.dms.ui.components.tags;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.docs.AttributeValue;
import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.ui.builder.BuilderFactory;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;

public class AttributeValueContainer extends VerticalLayout {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(AttributeValueContainer.class);

    private final List<AttributeValueField> fields;

    public AttributeValueContainer(BuilderFactory builderFactory, Doc doc) {
        fields = new ArrayList<>();

        setPadding(false);
        setSpacing(false);
        for (AttributeValue attributeValue : doc.getAttributeValues()) {
            AttributeValueField field = builderFactory.attributes().valueField(attributeValue).build();
            field.setWidthFull();
            add(field);
            fields.add(field);
        }
    }

    public boolean validate() {
        for (AttributeValueField field : fields) {
            if (!field.validate()) {
                LOGGER.showError("Attribute '" + field.getLabel() + " is required.");
                return false;
            }
        }
        return true;
    }
}