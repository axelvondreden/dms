package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.TextBlockService
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.dom.ElementFactory

class DocTextDialog(doc: Doc, textBlockService: TextBlockService) : Dialog() {

    init {
        val container = ElementFactory.createDiv().apply {
            style["width"] = "100%"
            style["height"] = "100%"
            style["position"] = "relative"
            style["maxWidth"] = "100%"
            style["maxHeight"] = "100%"
        }
        val verticalLayout = VerticalLayout().apply {
            setSizeFull()
            element.appendChild(container)
        }
        add(verticalLayout)

        var first = true
        for (textBlock in textBlockService.findByDoc(doc)) {
            if (first) {
                first = false
                setSize(textBlock.pageWidth, textBlock.pagHeight)
            }
            val div = ElementFactory.createDiv(textBlock.text).apply {
                style["fontSize"] = "${(textBlock.fontSize / 6.0f)}vmin"
                style["textAlign"] = "center"
                style["position"] = "absolute"
                style["top"] = "${textBlock.y}%"
                style["left"] = "${textBlock.x}%"
                style["width"] = "${textBlock.width}%"
                style["height"] = "${textBlock.height}%"
            }
            container.appendChild(div)
        }
    }

    private fun setSize(pageWidth: Float, pagHeight: Float) {
        width = "60vw"
        height = "${(pagHeight / pageWidth * 100.0f * 0.6f)}vh"
    }
}