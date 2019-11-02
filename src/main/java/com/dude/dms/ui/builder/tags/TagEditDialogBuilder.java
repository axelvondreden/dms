package com.dude.dms.ui.builder.tags;

import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityEventListener;
import com.dude.dms.ui.components.dialogs.TagEditDialog;

public final class TagEditDialogBuilder {

    private final Tag tag;

    private final TagService tagService;

    private EntityEventListener eventListener;

    TagEditDialogBuilder(Tag tag, TagService tagService) {
        this.tag = tag;
        this.tagService = tagService;
    }

    public TagEditDialogBuilder withEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public TagEditDialog build() {
        TagEditDialog dialog = new TagEditDialog(tag, tagService);
        dialog.setEventListener(eventListener);
        return dialog;
    }
}
