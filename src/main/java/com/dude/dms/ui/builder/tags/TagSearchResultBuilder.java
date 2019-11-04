package com.dude.dms.ui.builder.tags;

import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.ui.components.dialogs.TagEditDialog;
import com.dude.dms.ui.components.search.TagSearchResult;

public final class TagSearchResultBuilder {

    private final Tag tag;

    private final TagEditDialog tagEditDialog;

    private final DocService docService;

    TagSearchResultBuilder(Tag tag, TagEditDialog tagEditDialog, DocService docService) {
        this.tag = tag;
        this.tagEditDialog = tagEditDialog;
        this.docService = docService;
    }

    public TagSearchResult build() {
        return new TagSearchResult(tag, docService.countByTag(tag), tagEditDialog);
    }
}