package com.dude.dms.ui.components.cards

import com.dude.dms.backend.containers.DocContainer
import com.github.appreciated.card.Card
import com.github.appreciated.card.label.SecondaryLabel
import com.helger.commons.io.file.FileHelper
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.FlexComponent
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

    private fun fill() {
        val wrapper = VerticalLayout(label, imgDiv).apply {
            setSizeFull()
            alignItems = FlexComponent.Alignment.CENTER
            isPadding = false
            isSpacing = false
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