package com.dude.dms.ui.components.tags

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.TagService
import com.dude.dms.extensions.tagLabel
import com.dude.dms.extensions.tagLayout
import com.github.mvysny.karibudsl.v10.iconButton
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon

class TagLayout(
        private val tagService: TagService,
        var tags: MutableSet<Tag>,
        private val edit: Boolean = false,
        compact: Boolean = false
) : Div() {

    var onClick: ((ComponentEvent<*>) -> Unit)? = null

    private lateinit var pickList: TagLayout

    init {
        style["display"] = "flex"

        if (compact) {
            addClassName("tag-container-compact")
        } else {
            element.style["flexWrap"] = "wrap"
        }
        fill()
    }

    fun fill() {
        removeAll()
        if (edit) {
            iconButton(VaadinIcon.PLUS.create()) {
                onLeftClick { pickList.isVisible = true }
            }
            pickList = tagLayout(tagService, tagService.findAll().filter { tag -> tag.name !in tags.map { it.name } }.toMutableSet(), edit = false) {
                isVisible = false
                style["border"] = "2px solid black"
                onClick = {
                    it.source.children.findFirst().ifPresent { elem -> tagService.findByName(elem.element.text!!)?.let { tags.add(it) } }
                    fill()
                }
            }
        }
        tags.forEach {
            tagLabel(it) {
                onClick = this@TagLayout.onClick
            }
        }
    }
}