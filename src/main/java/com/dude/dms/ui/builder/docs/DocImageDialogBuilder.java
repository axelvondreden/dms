package com.dude.dms.ui.builder.docs;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.service.TextBlockService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.dialogs.DocImageDialog;

public final class DocImageDialogBuilder {

    private final BuilderFactory builderFactory;
    private final TextBlockService textBlockService;
    private final Doc doc;

    DocImageDialogBuilder(BuilderFactory builderFactory, Doc doc, TextBlockService textBlockService) {
        this.builderFactory = builderFactory;
        this.doc = doc;
        this.textBlockService = textBlockService;
    }

    public DocImageDialog build() {
        return new DocImageDialog(builderFactory, doc, textBlockService);
    }
}
