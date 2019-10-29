package com.dude.dms.ui.builder.dialogs;

import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityEventListener;
import com.dude.dms.ui.components.dialogs.TagCreateDialog;

public final class TagCreateDialogBuilder {

    private final TagService tagService;

    private EntityEventListener eventListener;

    TagCreateDialogBuilder(TagService tagService) {
        this.tagService = tagService;
    }

    public TagCreateDialogBuilder withEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public TagCreateDialog build() {
        TagCreateDialog dialog = new TagCreateDialog(tagService);
        dialog.setEventListener(eventListener);
        return dialog;
    }
}
