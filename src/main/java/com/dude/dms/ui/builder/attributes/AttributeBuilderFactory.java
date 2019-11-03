package com.dude.dms.ui.builder.attributes;

import com.dude.dms.backend.service.AttributeService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.builder.Factory;

public class AttributeBuilderFactory extends Factory {

    private final AttributeService attributeService;

    public AttributeBuilderFactory(BuilderFactory builderFactory, AttributeService attributeService) {
        super(builderFactory);
        this.attributeService = attributeService;
    }

    public AttributeSelectorBuilder selector() {
        return new AttributeSelectorBuilder(attributeService);
    }
}