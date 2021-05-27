package com.dude.dms.ui.components.tags

import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.brain.t
import com.dude.dms.utils.docService
import com.dude.dms.utils.tagService
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Label
import dev.mett.vaadin.tooltip.Tooltips

class TagSelector : Grid<TagContainer>() {

    var selectedTags: Set<TagContainer>
        get() = asMultiSelect().selectedItems
        set(tags) {
            setItems(tags.sortedBy { it.tag.name }.toSet().plus(tagService.findAll().map { TagContainer(it) }.sortedByDescending { docService.countByTag(it.tag) }.toSet()))
            asMultiSelect().deselectAll()
            asMultiSelect().select(tags)
        }

    init {
        setSelectionMode(SelectionMode.MULTI)
        if (selectedTags.isNullOrEmpty()) {
            setItems(tagService.findAll().map { TagContainer(it) }.sortedByDescending { docService.countByTag(it.tag) })
        }
        val tooltip = Tooltips.getCurrent()
        addComponentColumn { tagContainer ->
            Label(tagContainer.tag.name).apply { tagContainer.tagOrigin?.let { tooltip.setTooltip(this, it) } }
        }.setHeader(t("tags")).setKey("tag").isAutoWidth = true
        setSizeFull()
        addItemClickListener { event ->
            if (asMultiSelect().isSelected(event.item)) {
                deselect(event.item)
            } else {
                select(event.item)
            }
        }
    }
}