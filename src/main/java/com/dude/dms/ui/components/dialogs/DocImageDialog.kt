package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.brain.FileManager
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.TextBlockService
import com.dude.dms.ui.builder.BuilderFactory
import com.helger.commons.io.file.FileHelper
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.dom.Element
import com.vaadin.flow.dom.ElementFactory
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import dev.mett.vaadin.tooltip.Tooltips

class DocImageDialog(private val builderFactory: BuilderFactory, private val doc: Doc, private val textBlockService: TextBlockService) : Dialog() {

    private val container = ElementFactory.createDiv().apply {
        style["width"] = "100%"
        style["height"] = "100%"
        style["position"] = "relative"
        style["maxWidth"] = "100%"
        style["maxHeight"] = "100%"
    }

    init {
        val verticalLayout = VerticalLayout().apply {
            maxWidth = "80vw"
            maxHeight = "80vh"
            element.appendChild(container)
        }
        add(verticalLayout)
        fill()
    }

    private fun fill() {
        container.removeAllChildren()
        val img = FileManager.getDocImage(doc)
        if (img.exists()) {
            val image = Element("object").apply {
                setAttribute("type", "image/png")
                style["maxWidth"] = "100%"
                style["maxHeight"] = "100%"
                setAttribute("data", StreamResource("image.png", InputStreamFactory { FileHelper.getInputStream(img) }))
            }
            container.appendChild(image)
            for (textBlock in textBlockService.findByDoc(doc)) {
                val div = Div().apply {
                    element.style["border"] = "2px solid gray"
                    element.style["position"] = "absolute"
                    element.style["top"] = "${textBlock.y}%"
                    element.style["left"] = "${textBlock.x}%"
                    element.style["width"] = "${textBlock.width}%"
                    element.style["height"] = "${textBlock.height}%"
                    element.setAttribute("id", textBlock.id.toString())
                    element.addEventListener("mouseenter") { event -> event.source.style["border"] = "3px solid black" }
                    element.addEventListener("mouseleave") { event -> event.source.style["border"] = "2px solid gray" }
                    element.addEventListener("click") { event ->
                        builderFactory.docs().textBlockEditDialog(textBlockService.load(event.source.getAttribute("id").toLong())!!).build().open()
                    }
                }
                container.appendChild(div.element)
                Tooltips.getCurrent().setTooltip(div, textBlock.text)
            }
        } else {
            add(Text("No image found!"))
        }
    }
}