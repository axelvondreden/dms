package com.dude.dms.ui.builder.dialogs;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.service.TextBlockService;
import com.dude.dms.ui.components.dialogs.DocTextDialog;

public final class DocTextDialogBuilder {

    private final TextBlockService textBlockService;
    private final Doc doc;

    DocTextDialogBuilder(Doc doc, TextBlockService textBlockService) {
        this.doc = doc;
        this.textBlockService = textBlockService;
    }

    public DocTextDialog build() {
        return new DocTextDialog(doc, textBlockService);
    }
}
