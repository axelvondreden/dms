package com.dude.dms.ui.components.search;

import com.dude.dms.backend.data.tags.Tag;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.components.dialogs.crud.TagEditDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

public class TagSearchResult extends SearchResult {

    private final Tag tag;

    private final TagService tagService;

    public TagSearchResult(Tag tag, TagService tagService) {
        this.tag = tag;
        this.tagService = tagService;
    }

    @Override
    public String getHeader() {
        return "Tag " + tag.getName();
    }

    @Override
    public Component getBody() {
        return new Div();
    }

    @Override
    public void onClick() {
        new TagEditDialog(tagService).open(tag);
    }
}