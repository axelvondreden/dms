package com.dude.dms.ui.components.tags;

import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.service.TagService;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;

import java.util.List;
import java.util.Set;

public class TagSelector extends Grid<Tag> {

    public TagSelector(TagService tagService) {
        List<Tag> tags = tagService.findAll();

        setItems(tags);
        addColumn(Tag::getName).setHeader("Tags").setAutoWidth(true);
        setSizeFull();

        setSelectionMode(SelectionMode.MULTI);
        addItemClickListener(event -> {
            if (asMultiSelect().isSelected(event.getItem())) {
                deselect(event.getItem());
            } else {
                select(event.getItem());
            }
        });
    }

    public void setSelectedTags(Iterable<Tag> tags) {
        asMultiSelect().select(tags);
    }

    public void setContainedTags(String rawText) {
        addComponentColumn(tag -> {
            Checkbox checkbox = new Checkbox(rawText != null && rawText.contains(tag.getName()));
            checkbox.setReadOnly(true);
            return checkbox;
        }).setHeader("Contained").setAutoWidth(true);
    }

    public Set<Tag> getSelectedTags() {
        return asMultiSelect().getSelectedItems();
    }
}