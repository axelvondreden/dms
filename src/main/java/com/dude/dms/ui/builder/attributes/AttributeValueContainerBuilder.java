package com.dude.dms.ui.builder.attributes;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.tags.AttributeValueContainer;

public final class AttributeValueContainerBuilder {

    private final BuilderFactory builderFactory;

    private final Doc doc;

    AttributeValueContainerBuilder(BuilderFactory builderFactory, Doc doc) {
        this.builderFactory = builderFactory;
        this.doc = doc;
    }

    public AttributeValueContainer build() {
        return new AttributeValueContainer(builderFactory, doc);
    }
}