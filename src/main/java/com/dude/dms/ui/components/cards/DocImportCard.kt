package com.dude.dms.ui.components.cards

import com.dude.dms.backend.containers.DocContainer
import com.github.appreciated.card.Card
import com.github.appreciated.card.label.SecondaryLabel
import com.helger.commons.io.file.FileHelper
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.dom.Element
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource

class DocImportCard(val docContainer: DocContainer) : Card() {

    private val label = SecondaryLabel(docContainer.file?.name).apply {
        setAlignSelf(FlexComponent.Alignment.CENTER)
        width = null
        style["padding"] = "4px"
    }

    private val imgDiv = Div().apply {
        setWidthFull()
        element.style["overflow"] = "hidden"
        element.appendChild(Element("object").apply {
            setAttribute("attribute.type", "image/png")
            style["maxWidth"] = "100%"
            setAttribute("data", StreamResource("image.png", InputStreamFactory { FileHelper.getInputStream(docContainer.pages.first { it.nr == 1 }.image!!) }))
        })
    }

    init {
        setHeightFull()
        maxWidth = "250px"
        element.style["display"] = "block"
        element.style["padding"] = "0px 0px 8px"
        fill()
    }

    fun clear() {
        content.removeAll()
    }

    fun fill() {
        clear()
        val wrapper = VerticalLayout(
                HorizontalLayout(
                        label,
                        Label(docContainer.tags.size.toString()).apply { style["margin"] = "auto 0px auto auto" },
                        Icon(VaadinIcon.TAGS).apply {
                            style["maxWidth"] = "15px"
                            style["margin"] = "auto 0px auto auto"
                        },
                        Label(docContainer.pages.size.toString()).apply { style["margin"] = "auto 0px auto auto" },
                        Icon(VaadinIcon.FILE_TEXT).apply {
                            style["maxWidth"] = "15px"
                            style["margin"] = "2px"
                        }
                ).apply {
                    setWidthFull()
                    alignItems = FlexComponent.Alignment.CENTER
                    isPadding = false
                    isSpacing = false
                },
                imgDiv
        ).apply {
            setSizeFull()
            alignItems = FlexComponent.Alignment.CENTER
            isPadding = false
            isSpacing = false
            if (docContainer.done) style["backgroundColor"] = "rgba(0, 255, 0, 0.3)"
        }
        add(wrapper)
    }

    fun select(selected: Boolean) {
        if (selected) {
            style["border"] = "4px solid darkGray"
            element.executeJs("this.scrollIntoView();")
        } else {
            style["border"] = null
        }
    }
}