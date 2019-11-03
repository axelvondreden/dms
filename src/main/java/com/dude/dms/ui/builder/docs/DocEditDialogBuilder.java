package com.dude.dms.ui.builder.docs;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityEventListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.dialogs.DocEditDialog;

public final class DocEditDialogBuilder {

    private final BuilderFactory builderFactory;

    private final Doc doc;
    private final DocService docService;
    private final TagService tagService;

    private EntityEventListener eventListener;

    DocEditDialogBuilder(BuilderFactory builderFactory, Doc doc, DocService docService, TagService tagService) {
        this.builderFactory = builderFactory;
        this.doc = doc;
        this.docService = docService;
        this.tagService = tagService;
    }

    public DocEditDialogBuilder withEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public DocEditDialog build() {
        DocEditDialog dialog = new DocEditDialog(builderFactory, doc, docService);
        dialog.setEventListener(eventListener);
        return dialog;
    }
}
