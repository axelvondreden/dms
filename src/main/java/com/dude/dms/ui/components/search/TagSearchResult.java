package com.dude.dms.ui.components.search;

import com.dude.dms.backend.data.tags.Tag;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.components.dialogs.crud.TagEditDialog;
import com.github.appreciated.card.label.PrimaryLabel;
import com.github.appreciated.card.label.TitleLabel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class TagSearchResult extends SearchResult {

    private final Tag tag;

    private final TagService tagService;
    private final DocService docService;

    public TagSearchResult(Tag tag, TagService tagService, DocService docService) {
        this.tag = tag;
        this.tagService = tagService;
        this.docService = docService;
    }

    @Override
    public String getHeader() {
        return "Tag";
    }

    @Override
    public Component getBody() {
        HorizontalLayout h = new HorizontalLayout();
        h.setWidthFull();
        h.add(new TitleLabel(tag.getName()), new PrimaryLabel(docService.countByTag(tag) + " Documents"));
        h.setAlignItems(FlexComponent.Alignment.CENTER);
        h.getElement().getStyle().set("border", "5px solid " + tag.getColor());
        return h;
    }

    @Override
    public void onClick() {
        new TagEditDialog(tagService).open(tag);
    }
}