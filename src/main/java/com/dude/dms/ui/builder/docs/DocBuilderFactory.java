package com.dude.dms.ui.builder.docs;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.docs.TextBlock;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.backend.service.TextBlockService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.builder.Factory;

public class DocBuilderFactory extends Factory {

    private final DocService docService;
    private final TagService tagService;
    private final TextBlockService textBlockService;

    public DocBuilderFactory(BuilderFactory builderFactory, DocService docService, TagService tagService, TextBlockService textBlockService) {
        super(builderFactory);
        this.docService = docService;
        this.tagService = tagService;
        this.textBlockService = textBlockService;
    }

    public DocSearchResultBuilder searchResult(Doc doc, String search) {
        return new DocSearchResultBuilder(doc, search, builderFactory.docs().imageDialog(doc).build(), builderFactory.docs().textDialog(doc).build());
    }

    public DocCreateDialogBuilder createDialog() {
        return new DocCreateDialogBuilder();
    }

    public DocEditDialogBuilder editDialog(Doc doc) {
        return new DocEditDialogBuilder(doc, docService, tagService);
    }

    public DocImageDialogBuilder imageDialog(Doc doc) {
        return new DocImageDialogBuilder(builderFactory, doc, textBlockService);
    }

    public DocTextDialogBuilder textDialog(Doc doc) {
        return new DocTextDialogBuilder(doc, textBlockService);
    }

    public TextBlockEditDialogBuilder textBlockEditDialog(TextBlock textBlock) {
        return new TextBlockEditDialogBuilder(textBlock, textBlockService);
    }
}