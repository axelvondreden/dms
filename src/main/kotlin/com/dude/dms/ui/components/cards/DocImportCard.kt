package com.dude.dms.ui.components.cards

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.utils.secondaryLabel
import com.github.appreciated.card.Card
import com.github.mvysny.karibudsl.v10.*
import com.helger.commons.io.file.FileHelper
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.dom.Element
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource

class DocImportCard(val docContainer: DocContainer) : Card() {

    init {
        setHeightFull()
        maxWidth = "250px"
        element.style["display"] = "block"
        element.style["padding"] = "0px 0px 8px"
        fill()
    }

    fun fill() {
        content.removeAll()

        verticalLayout(isPadding = false, isSpacing = false) {
            setSizeFull()
            alignItems = FlexComponent.Alignment.CENTER
            if (docContainer.done) style["backgroundColor"] = "rgba(0, 255, 0, 0.3)"

            horizontalLayout(isPadding = false, isSpacing = false) {
                setWidthFull()
                alignItems = FlexComponent.Alignment.CENTER

                secondaryLabel(docContainer.file?.name) {
                    setAlignSelf(FlexComponent.Alignment.CENTER)
                    width = null
                    style["padding"] = "4px"
                }
                label(docContainer.tags.size.toString()) { style["margin"] = "auto 0px auto auto" }
                icon(VaadinIcon.TAGS) {
                    style["maxWidth"] = "15px"
                    style["margin"] = "auto 0px auto auto"
                }
                label(docContainer.getPages().size.toString()) { style["margin"] = "auto 0px auto auto" }
                icon(VaadinIcon.FILE_TEXT) {
                    style["maxWidth"] = "15px"
                    style["margin"] = "2px"
                }
            }
            div {
                setWidthFull()
                element.style["overflow"] = "hidden"
                element.appendChild(Element("object").apply {
                    setAttribute("attribute.type", "image/png")
                    style["maxWidth"] = "100%"
                    setAttribute("data", StreamResource("image.png", InputStreamFactory { FileHelper.getInputStream(docContainer.thumbnail) }))
                })
            }
        }
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
