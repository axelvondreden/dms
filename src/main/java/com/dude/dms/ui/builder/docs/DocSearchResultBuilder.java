package com.dude.dms.ui.builder.docs;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.ui.components.dialogs.DocImageDialog;
import com.dude.dms.ui.components.dialogs.DocTextDialog;
import com.dude.dms.ui.components.search.DocSearchResult;

public final class DocSearchResultBuilder {

    private final Doc doc;

    private final String search;

    private final DocImageDialog docImageDialog;
    private final DocTextDialog docTextDialog;

    DocSearchResultBuilder(Doc doc, String search, DocImageDialog docImageDialog, DocTextDialog docTextDialog) {
        this.doc = doc;
        this.search = search;
        this.docImageDialog = docImageDialog;
        this.docTextDialog = docTextDialog;
    }

    public DocSearchResult build() {
        return new DocSearchResult(doc, search, docImageDialog, docTextDialog);
    }
}