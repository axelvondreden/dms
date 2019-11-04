package com.dude.dms.ui.builder.attributes;

import com.dude.dms.backend.data.docs.AttributeValue;
import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.service.AttributeService;
import com.dude.dms.backend.service.AttributeValueService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.builder.Factory;

public class AttributeBuilderFactory extends Factory {

    private final AttributeService attributeService;

    private final AttributeValueService attributeValueService;

    public AttributeBuilderFactory(BuilderFactory builderFactory, AttributeService attributeService, AttributeValueService attributeValueService) {
        super(builderFactory);
        this.attributeService = attributeService;
        this.attributeValueService = attributeValueService;
    }

    public AttributeSelectorBuilder selector() {
        return new AttributeSelectorBuilder(attributeService);
    }

    public AttributeValueContainerBuilder valueContainer(Doc doc) {
        return new AttributeValueContainerBuilder(builderFactory, doc);
    }

    public AttributeValueFieldBuilder valueField(AttributeValue attributeValue) {
        return new AttributeValueFieldBuilder(attributeValue, attributeValueService);
    }
}