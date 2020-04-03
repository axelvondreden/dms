package com.dude.dms.ui.components.tags

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.t
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.grid.Grid

class TagSelector(tagService: TagService) : Grid<Tag>() {

    var selectedTags: Set<Tag>
        get() = asMultiSelect().selectedItems
        set(tags) {
            asMultiSelect().select(tags)
        }

    init {
        setItems(tagService.findAll())
        addColumn { it.name }.setHeader(t("tags")).isAutoWidth = true
        setSizeFull()
        setSelectionMode(SelectionMode.MULTI)
        addItemClickListener { event ->
            if (asMultiSelect().isSelected(event.item)) {
                deselect(event.item)
            } else {
                select(event.item)
            }
        }
    }

    fun setContainedTags(rawText: String?) {
        addComponentColumn { Checkbox(rawText != null && rawText.contains(it.name)).apply { isReadOnly = true } }.setHeader(t("contained")).isAutoWidth = true
    }
}