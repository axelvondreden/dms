package com.dude.dms.ui.builder.tags;

import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityCreateListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.dialogs.TagCreateDialog;

public final class TagCreateDialogBuilder {

    private final BuilderFactory builderFactory;

    private final TagService tagService;

    private EntityCreateListener<Tag> createListener;

    TagCreateDialogBuilder(BuilderFactory builderFactory, TagService tagService) {
        this.builderFactory = builderFactory;
        this.tagService = tagService;
    }

    public TagCreateDialogBuilder withCreateListener(EntityCreateListener<Tag> createListener) {
        this.createListener = createListener;
        return this;
    }

    public TagCreateDialog build() {
        TagCreateDialog dialog = new TagCreateDialog(builderFactory, tagService);
        dialog.setCreateListener(createListener);
        return dialog;
    }
}
