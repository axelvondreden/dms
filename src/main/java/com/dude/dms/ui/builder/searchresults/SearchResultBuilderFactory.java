package com.dude.dms.ui.builder.searchresults;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.builder.Factory;

public class SearchResultBuilderFactory extends Factory {

    private final DocService docService;

    public SearchResultBuilderFactory(BuilderFactory builderFactory, DocService docService) {
        super(builderFactory);
        this.docService = docService;
    }

    public DocSearchResultBuilder doc(Doc doc, String search) {
        return new DocSearchResultBuilder(doc, search, builderFactory.dialogs().docImage(doc).build(), builderFactory.dialogs().docText(doc).build());
    }

    public TagSearchResultBuilder tag(Tag tag) {
        return new TagSearchResultBuilder(tag, builderFactory.dialogs().tagEdit(tag).build(), docService);
    }
}