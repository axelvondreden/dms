package com.dude.dms.ui.components.search

import com.dude.dms.backend.data.Tag
import com.dude.dms.brain.t
import com.dude.dms.ui.components.dialogs.TagEditDialog
import com.github.appreciated.card.label.PrimaryLabel
import com.github.appreciated.card.label.TitleLabel
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class TagSearchResult(private val tag: Tag, private val count: Long, private val tagEditDialog: TagEditDialog) : SearchResult() {

    override val header = t("tag")

    override val body = HorizontalLayout().apply {
        setWidthFull()
        add(TitleLabel(tag.name), PrimaryLabel("${t("docs")}: $count"))
        alignItems = FlexComponent.Alignment.CENTER
        element.style["border"] = "5px solid " + tag.color
    }

    override fun onClick() {
        tagEditDialog.open()
    }
}