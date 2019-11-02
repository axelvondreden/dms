package com.dude.dms.ui.builder.attributes;

import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.builder.Factory;

public class AttributeBuilderFactory extends Factory {

    private final TagService tagService;

    public AttributeBuilderFactory(BuilderFactory builderFactory, TagService tagService) {
        super(builderFactory);
        this.tagService = tagService;
    }


}