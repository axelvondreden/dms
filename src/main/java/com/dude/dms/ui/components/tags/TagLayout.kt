package com.dude.dms.ui.components.tags

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.TagService
import com.dude.dms.ui.builder.BuilderFactory
import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon

class TagLayout(
        private val builderFactory: BuilderFactory,
        private var tags: MutableSet<Tag>,
        private val tagService: TagService,
        private val edit: Boolean = false,
        compact: Boolean = false,
        private val onClick: ((ComponentEvent<*>) -> Unit)? = null
) : Div() {

    init {
        element.style["display"] = "flex"

        if (compact) {
            addClassName("tag-container-compact")
        } else {
            element.style["flexWrap"] = "wrap"
        }
        fill()
    }

    fun setTags(tags: MutableSet<Tag>) {
        this.tags = tags
        fill()
    }

    fun fill() {
        removeAll()
        if (edit) {
            val pickList = builderFactory.tags().container(tagService.findAll().filter { tag -> tag.name !in tags.map { it.name } }.toMutableSet(), false) { event ->
                event.source.children.findFirst().ifPresent { elem -> tagService.findByName(elem.element.text!!)?.let { tags.add(it) } }
                fill()
            }.apply { isVisible = false; element.style["border"] = "2px solid black" }
            val plus = Button(VaadinIcon.PLUS.create()) {
                pickList.isVisible = true
            }
            add(plus, pickList)
        }
        tags.forEach { add(TagLabel(it, onClick)) }
    }
}