package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.service.LineService
import com.dude.dms.backend.service.WordService
import com.dude.dms.brain.FileManager
import com.dude.dms.ui.builder.BuilderFactory
import com.helger.commons.io.file.FileHelper
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
        private val lineService: LineService,
        private val wordService: WordService,
        private val fileManager: FileManager,
        private val doc: Doc? = null,
        private val guid: String? = null,
        private val lines: Set<Line> = emptySet()
) : Dialog() {

    private val container = ElementFactory.createDiv().apply { classList.add("image-container") }

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
        val img = fileManager.getFirstImage((doc?.guid ?: guid)!!)
        if (!img.exists()) return
        val image = Element("object").apply {
            setAttribute("attribute.type", "image/png")
            classList.add("inline-image")
            setAttribute("data", StreamResource("image.png", InputStreamFactory { FileHelper.getInputStream(img) }))
            //addEventListener("mousedown") { mouseDown(it) }.addEventData("event.clientX").addEventData("element.clientY")
        }
        container.appendChild(image)
        for (line in doc?.let { lineService.findByDoc(it) } ?: lines) {
            for (word in doc?.let { wordService.findByLine(line) } ?: line.words) {
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
                        builderFactory.docs().wordEditDialog(
                                doc?.let { wordService.load(event.source.getAttribute("id").toLong())!! } ?: word, doc, lines
                        ).also {
                            it.addDialogCloseActionListener { Tooltips.getCurrent().setTooltip(this, word.text) }
                        }.open()
                    }
                }
                container.appendChild(div.element)
                Tooltips.getCurrent().setTooltip(div, word.text)
            }
        }
    }
}