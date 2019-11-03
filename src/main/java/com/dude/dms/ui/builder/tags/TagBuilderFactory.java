package com.dude.dms.ui.builder.tags;

import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.builder.Factory;

public class TagBuilderFactory extends Factory {

    private final TagService tagService;
    private final DocService docService;

    public TagBuilderFactory(BuilderFactory builderFactory, TagService tagService, DocService docService) {
        super(builderFactory);
        this.tagService = tagService;
        this.docService = docService;
    }

    public TagSearchResultBuilder searchResult(Tag tag) {
        return new TagSearchResultBuilder(tag, builderFactory.tags().editDialog(tag).build(), docService);
    }

    public TagCreateDialogBuilder createDialog() {
        return new TagCreateDialogBuilder(builderFactory, tagService);
    }

    public TagEditDialogBuilder editDialog(Tag tag) {
        return new TagEditDialogBuilder(builderFactory, tag, tagService);
    }

    public TagSelectorBuilder selector() {
        return new TagSelectorBuilder(tagService);
    }
}