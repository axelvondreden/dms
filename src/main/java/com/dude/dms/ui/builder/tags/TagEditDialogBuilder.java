package com.dude.dms.ui.builder.tags;

import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityEditListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.dialogs.TagEditDialog;

public final class TagEditDialogBuilder {

    private final BuilderFactory builderFactory;

    private final Tag tag;

    private final TagService tagService;

    private final DocService docService;

    private EntityEditListener<Tag> editListener;

    TagEditDialogBuilder(BuilderFactory builderFactory, Tag tag, TagService tagService, DocService docService) {
        this.builderFactory = builderFactory;
        this.tag = tag;
        this.tagService = tagService;
        this.docService = docService;
    }

    public TagEditDialogBuilder withEditListener(EntityEditListener<Tag> editListener) {
        this.editListener = editListener;
        return this;
    }

    public TagEditDialog build() {
        TagEditDialog dialog = new TagEditDialog(builderFactory, tag, tagService, docService);
        dialog.setEditListener(editListener);
        return dialog;
    }
}
