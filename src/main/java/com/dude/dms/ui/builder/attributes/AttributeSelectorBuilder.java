package com.dude.dms.ui.builder.attributes;

import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.data.docs.Attribute;
import com.dude.dms.backend.service.AttributeService;
import com.dude.dms.ui.components.tags.AttributeSelector;

import java.util.HashSet;
import java.util.Set;

public final class AttributeSelectorBuilder {

    private final AttributeService attributeService;

    private Set<Attribute> selected;

    AttributeSelectorBuilder(AttributeService attributeService) {
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
        AttributeSelector attributeSelector = new AttributeSelector(attributeService);
        attributeSelector.setSelectedAttributes(selected);
        return attributeSelector;
    }
}