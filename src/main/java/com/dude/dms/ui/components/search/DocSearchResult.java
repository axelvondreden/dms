package com.dude.dms.ui.components.search;

import com.dude.dms.backend.data.base.Doc;

public class DocSearchResult extends SearchResult {

    private final Doc doc;

    public DocSearchResult(Doc doc) {
        this.doc = doc;
    }

    @Override
    public String getHeader() {
        return "Doc";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSearchtext() {
        return doc.getRawText();
    }
}
