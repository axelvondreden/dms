package com.dude.dms.ui.builder.dialogs;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityEventListener;
import com.dude.dms.ui.components.dialogs.DocEditDialog;

public final class DocEditDialogBuilder {

    private final Doc doc;
    private final DocService docService;
    private final TagService tagService;

    private EntityEventListener eventListener;

    DocEditDialogBuilder(Doc doc, DocService docService, TagService tagService) {
        this.doc = doc;
        this.docService = docService;
        this.tagService = tagService;
    }

    public DocEditDialogBuilder withEventListener(EntityEventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public DocEditDialog build() {
        DocEditDialog dialog = new DocEditDialog(doc, docService, tagService);
        dialog.setEventListener(eventListener);
        return dialog;
    }
}
