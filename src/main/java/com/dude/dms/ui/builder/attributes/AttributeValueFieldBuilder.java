package com.dude.dms.ui.builder.attributes;

import com.dude.dms.backend.data.docs.AttributeValue;
import com.dude.dms.backend.service.AttributeValueService;
import com.dude.dms.ui.components.tags.AttributeValueField;

public final class AttributeValueFieldBuilder {

    private final AttributeValue attributeValue;

    private final AttributeValueService attributeValueService;

    AttributeValueFieldBuilder(AttributeValue attributeValue, AttributeValueService attributeValueService) {
        this.attributeValue = attributeValue;
        this.attributeValueService = attributeValueService;
    }

    public AttributeValueField build() {
        return new AttributeValueField(attributeValue, attributeValueService);
    }
}