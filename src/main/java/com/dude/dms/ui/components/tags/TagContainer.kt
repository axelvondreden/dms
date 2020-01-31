package com.dude.dms.ui.components.tags

import com.dude.dms.backend.data.Tag
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class TagContainer(private var tags: Iterable<Tag>) : HorizontalLayout() {

    init {
        element.style["display"] = "inlineBlock"
        fill()
    }

    fun setTags(tags: Iterable<Tag>) {
        this.tags = tags
        fill()
    }

    fun fill() {
        removeAll()
        tags.forEach { add(TagLabel(it)) }
    }
}