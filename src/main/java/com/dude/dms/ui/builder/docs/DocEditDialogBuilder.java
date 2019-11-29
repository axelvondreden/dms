package com.dude.dms.ui.builder.docs;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.ui.EntityEditListener;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.dialogs.DocEditDialog;

public final class DocEditDialogBuilder {

    private final BuilderFactory builderFactory;

    private final Doc doc;
    private final DocService docService;

    private EntityEditListener<Doc> editListener;

    DocEditDialogBuilder(BuilderFactory builderFactory, Doc doc, DocService docService) {
        this.builderFactory = builderFactory;
        this.doc = doc;
        this.docService = docService;
    }

    public DocEditDialogBuilder withEditEventListener(EntityEditListener<Doc> editListener) {
        this.editListener = editListener;
        return this;
    }

    public DocEditDialog build() {
        DocEditDialog dialog = new DocEditDialog(builderFactory, doc, docService);
        dialog.setEditListener(editListener);
        return dialog;
    }
}
