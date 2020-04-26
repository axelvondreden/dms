package com.dude.dms.ui.components.tags

import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.t
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Label
import dev.mett.vaadin.tooltip.Tooltips

class TagSelector(private val tagService: TagService) : Grid<TagContainer>() {

    var selectedTags: Set<TagContainer>
        get() = asMultiSelect().selectedItems
        set(tags) {
            setItems(tags.sortedBy { it.tag.name }.toSet().plus(tagService.findAll().map { TagContainer(it) }.sortedBy { it.tag.name }.toSet()))
            asMultiSelect().deselectAll()
            asMultiSelect().select(tags)
        }

    var rawText: String? = null

    init {
        setSelectionMode(SelectionMode.MULTI)
        if (selectedTags.isNullOrEmpty()) {
            setItems(tagService.findAll().map { TagContainer(it) }.sortedBy { it.tag.name })
        }
        val tooltip = Tooltips.getCurrent()
        addComponentColumn { tagContainer ->
            Label(tagContainer.tag.name).apply { tagContainer.tagOrigin?.let { tooltip.setTooltip(this, it) } }
        }.setHeader(t("tags")).setKey("tag").isAutoWidth = true
        addComponentColumn {
            Checkbox(rawText?.contains(it.tag.name) ?: false).apply { isReadOnly = true }
        }.setHeader(t("contained")).setAutoWidth(true).setKey("contained").isVisible = false
        setSizeFull()
        addItemClickListener { event ->
            if (asMultiSelect().isSelected(event.item)) {
                deselect(event.item)
            } else {
                select(event.item)
            }
        }
    }

    fun showContainedTags(show: Boolean) {
        getColumnByKey("contained").isVisible = show
    }
}