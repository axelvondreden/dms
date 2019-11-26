package com.dude.dms.ui.builder.tags;

import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityEventListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.dialogs.TagEditDialog;

public final class TagEditDialogBuilder {

    private final BuilderFactory builderFactory;

    private final Tag tag;

    private final TagService tagService;

    private final DocService docService;

    private EntityEventListener eventListener;

    TagEditDialogBuilder(BuilderFactory builderFactory, Tag tag, TagService tagService, DocService docService) {
        this.builderFactory = builderFactory;
        this.tag = tag;
        this.tagService = tagService;
        this.docService = docService;
    }

    public TagEditDialogBuilder withEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public TagEditDialog build() {
        TagEditDialog dialog = new TagEditDialog(builderFactory, tag, tagService, docService);
        dialog.setEventListener(eventListener);
        return dialog;
    }
}
