package com.dude.dms.ui.components.dialogs

import com.dude.dms.brain.FileManager
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.LineService
import com.dude.dms.backend.service.WordService
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

class DocImageDialog(
        private val builderFactory: BuilderFactory,
        private val doc: Doc,
        private val lineService: LineService,
        private val wordService: WordService,
        private val fileManager: FileManager
) : Dialog() {

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
        val img = fileManager.getFirstImage(doc.guid)
        if (img.exists()) {
            val image = Element("object").apply {
                setAttribute("type", "image/png")
                style["maxWidth"] = "100%"
                style["maxHeight"] = "100%"
                setAttribute("data", StreamResource("image.png", InputStreamFactory { FileHelper.getInputStream(img) }))
            }
            container.appendChild(image)
            for (line in lineService.findByDoc(doc)) {
                for (word in wordService.findByLine(line)) {
                    val div = Div().apply {
                        element.style["border"] = "2px solid gray"
                        element.style["position"] = "absolute"
                        element.style["top"] = "${word.y}%"
                        element.style["left"] = "${word.x}%"
                        element.style["width"] = "${word.width}%"
                        element.style["height"] = "${word.height}%"
                        element.setAttribute("id", word.id.toString())
                        element.addEventListener("mouseenter") { event -> event.source.style["border"] = "3px solid black" }
                        element.addEventListener("mouseleave") { event -> event.source.style["border"] = "2px solid gray" }
                        element.addEventListener("click") { event ->
                            builderFactory.docs().wordEditDialog(doc, wordService.load(event.source.getAttribute("id").toLong())!!).build().open()
                        }
                    }
                    container.appendChild(div.element)
                    Tooltips.getCurrent().setTooltip(div, word.text)
                }
            }
        } else {
            add(Text("No image found!"))
        }
    }
}