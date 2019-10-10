package com.dude.dms.ui.components.tags;

import com.dude.dms.backend.data.base.Tag;
import com.dude.dms.backend.service.TagService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.MultiSelect;

import java.util.List;
import java.util.Set;

public class Tagger extends Grid<Tag> {

    private final List<Tag> tags;

    public Tagger(TagService tagService) {
        tags = tagService.findAll();

        setItems(tags);
        addColumn(Tag::getName).setHeader("Tags");
        setSizeFull();

        setSelectionMode(SelectionMode.MULTI);
    }

    public void setSelectedTags(Iterable<Tag> tags) {
        asMultiSelect().select(tags);
    }

    public void clear() {
        asMultiSelect().clear();
    }

    public Set<Tag> getSelectedTags() {
        return asMultiSelect().getSelectedItems();
    }
}