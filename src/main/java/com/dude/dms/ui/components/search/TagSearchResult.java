package com.dude.dms.ui.components.search;

import com.dude.dms.backend.data.tags.Tag;
import com.dude.dms.ui.components.dialogs.TagEditDialog;
import com.github.appreciated.card.label.PrimaryLabel;
import com.github.appreciated.card.label.TitleLabel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class TagSearchResult extends SearchResult {

    private final Tag tag;

    private final long count;

    private final TagEditDialog tagEditDialog;

    public TagSearchResult(Tag tag, long count, TagEditDialog tagEditDialog) {
        this.tag = tag;
        this.count = count;
        this.tagEditDialog = tagEditDialog;
    }

    @Override
    public String getHeader() {
        return "Tag";
    }

    @Override
    public Component getBody() {
        HorizontalLayout h = new HorizontalLayout();
        h.setWidthFull();
        h.add(new TitleLabel(tag.getName()), new PrimaryLabel(count + " Documents"));
        h.setAlignItems(FlexComponent.Alignment.CENTER);
        h.getElement().getStyle().set("border", "5px solid " + tag.getColor());
        return h;
    }

    @Override
    public void onClick() {
        tagEditDialog.open();
    }
}