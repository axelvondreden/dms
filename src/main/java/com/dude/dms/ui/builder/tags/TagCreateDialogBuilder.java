package com.dude.dms.ui.builder.tags;

import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityEventListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.dialogs.TagCreateDialog;

public final class TagCreateDialogBuilder {

    private final BuilderFactory builderFactory;

    private final TagService tagService;

    private EntityEventListener eventListener;

    TagCreateDialogBuilder(BuilderFactory builderFactory, TagService tagService) {
        this.builderFactory = builderFactory;
        this.tagService = tagService;
    }

    public TagCreateDialogBuilder withEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public TagCreateDialog build() {
        TagCreateDialog dialog = new TagCreateDialog(builderFactory, tagService);
        dialog.setEventListener(eventListener);
        return dialog;
    }
}
