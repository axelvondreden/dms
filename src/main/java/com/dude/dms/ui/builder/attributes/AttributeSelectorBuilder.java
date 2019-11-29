package com.dude.dms.ui.builder.attributes;

import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.data.docs.Attribute;
import com.dude.dms.backend.service.AttributeService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.tags.AttributeSelector;

import java.util.HashSet;
import java.util.Set;

public final class AttributeSelectorBuilder {

    private final BuilderFactory builderFactory;

    private final AttributeService attributeService;

    private Set<Attribute> selected;

    AttributeSelectorBuilder(BuilderFactory builderFactory, AttributeService attributeService) {
        this.builderFactory = builderFactory;
        this.attributeService = attributeService;
        selected = new HashSet<>();
    }

    public AttributeSelectorBuilder forTag(Tag tag) {
        return withSelectedAttributes(tag.getAttributes());
    }

    public AttributeSelectorBuilder withSelectedAttributes(Set<Attribute> selected) {
        this.selected = selected;
        return this;
    }

    public AttributeSelector build() {
        AttributeSelector attributeSelector = new AttributeSelector(builderFactory, attributeService);
        attributeSelector.setSelectedAttributes(selected);
        return attributeSelector;
    }
}