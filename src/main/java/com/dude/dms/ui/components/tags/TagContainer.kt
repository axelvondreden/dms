package com.dude.dms.ui.components.tags

import com.dude.dms.backend.data.Tag
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class TagContainer(tags: Iterable<Tag>) : HorizontalLayout() {

    init {
        element.style["display"] = "inlineBlock"
        tags.forEach { add(TagLabel(it)) }
    }
}