package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.data.docs.Word
import com.dude.dms.backend.service.LineService
import com.dude.dms.backend.service.WordService
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.helger.commons.io.file.FileHelper
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.dom.DomEvent
import com.vaadin.flow.dom.Element
import com.vaadin.flow.dom.ElementFactory
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import dev.mett.vaadin.tooltip.Tooltips
import kotlin.math.abs

class DocImageDialog(
        private val builderFactory: BuilderFactory,
        private val lineService: LineService,
        private val wordService: WordService,
        private val fileManager: FileManager,
        private val docParser: DocParser,
        private val doc: Doc? = null,
        private val guid: String? = null,
        private val lines: Set<Line> = emptySet()
) : Dialog() {

    private val container = ElementFactory.createDiv().apply {
        classList.add("image-container")
    }

    private val zoomButton = Button("100%") { resetZoom() }

    private val deleteButton = Button(VaadinIcon.TRASH.create()) { deleteSelected() }.apply { isEnabled = false }

    private var zoom = 100

    private val drawDiv = Div().apply {
        addClassName("draw-div")
    }

    private val words = mutableSetOf<Pair<Word, Div>>()

    private val selected = mutableSetOf<Pair<Word, Div>>()

    private var drawing = false
    private var selecting = false
    private var mouseStart = 0L
    private var mouseXStart = 0.0
    private var mouseYStart = 0.0
    private var mouseX = 0.0
    private var mouseY = 0.0
    private var mouseWidth = 0.0
    private var mouseHeight = 0.0

    init {
        val shrinkButton = Button(VaadinIcon.MINUS_CIRCLE.create()) { shrink() }
        val growButton = Button(VaadinIcon.PLUS_CIRCLE.create()) { grow() }
        val horizontalLayout = HorizontalLayout(shrinkButton, zoomButton, growButton).apply {
            element.style.set("position", "fixed")["zIndex"] = "11"
        }
        val verticalLayout = VerticalLayout().apply {
            maxWidth = "80vw"
            maxHeight = "80vh"
            element.appendChild(container)
        }
        add(horizontalLayout, verticalLayout)
        fill()
    }

    private fun resetZoom() {
        zoom = 100
        zoom()
    }

    private fun grow() {
        if (zoom < 1000) zoom += 20
        zoom()
    }

    private fun shrink() {
        if (zoom > 40) zoom -= 20
        zoom()
    }

    private fun zoom() {
        container.style["width"] = "$zoom%"
        zoomButton.text = "$zoom%"
    }

    private fun deleteSelected() {
        selected.forEach { pair ->
            wordService.delete(pair.first)
            lines.forEach { it.words = it.words.minus(pair.first) }
            container.removeChild(pair.second.element)
        }
        clearSelection()
    }

    private fun fill() {
        container.removeAllChildren()
        val img = fileManager.getFirstImage((doc?.guid ?: guid)!!)
        if (!img.exists()) return
        val image = Element("img").apply {
            classList.add("inline-image")
            setAttribute("src", StreamResource("image.png", InputStreamFactory { FileHelper.getInputStream(img) }))
            setAttribute("ondragstart", "return false;")
            addEventListener("mousedown") { mouseDown(it, this) }
                    .addEventData("event.offsetX").addEventData("event.offsetY").addEventData("event.button")
            addEventListener("mousemove") { mouseMove(it, this) }
                    .addEventData("event.offsetX").addEventData("event.offsetY")
            addEventListener("mouseup") { mouseUp() }
        }
        container.appendChild(image)
        for (line in doc?.let { lineService.findByDoc(it) } ?: lines) {
            for (word in doc?.let { wordService.findByLine(line) } ?: line.words) {
                addWordWrapper(word)
            }
        }
        container.appendChild(drawDiv.element)
    }

    private fun addWordWrapper(word: Word) {
        val dlg = builderFactory.docs().wordEditDialog(
                (if (word.id > 0) wordService.load(word.id) else word)!!, doc, lines
        )
        val div = Div().apply {
            addClassName("word-container")
            element.addEventListener("click") { dlg.open() }
        }
        val delBtn = Button(VaadinIcon.TRASH.create()).apply {
            addThemeVariants(ButtonVariant.LUMO_CONTRAST)
            addClassName("word-dropdown-button")
            Tooltips.getCurrent().setTooltip(this, t("delete"))
        }
        val ocrBtn = Button(VaadinIcon.CROSSHAIRS.create()).apply {
            addThemeVariants(ButtonVariant.LUMO_CONTRAST)
            addClassName("word-dropdown-button")
            Tooltips.getCurrent().setTooltip(this, t("ocr.run"))
        }
        val dropdown = HorizontalLayout(delBtn, ocrBtn).apply {
            addClassName("word-dropdown")
            isPadding = false
            isMargin = false
            isSpacing = false
        }
        val wrapper = Div(div, dropdown).apply {
            addClassName("word-wrapper")
            element.style["top"] = "${word.y}%"
            element.style["left"] = "${word.x}%"
            element.style["width"] = "${word.width}%"
            element.style["height"] = "${word.height}%"
            addClickListener {
                clearSelection()
                addClassName("word-wrapper-selected")
                addSelection(listOf(word to this))
            }
        }
        delBtn.addClickListener {
            wordService.delete(word)
            lines.forEach { it.words = it.words.minus(word) }
            container.removeChild(wrapper.element)
        }
        ocrBtn.addClickListener {
            word.text = docParser.getOcrTextRect(fileManager.getFirstImage((doc?.guid ?: guid)!!), word.x, word.y, word.width, word.height)
            wordService.save(word)
            Tooltips.getCurrent().setTooltip(wrapper, word.text)
        }
        dlg.addOpenedChangeListener {
            if (!it.isOpened) {
                Tooltips.getCurrent().setTooltip(wrapper, (if (word.id > 0) wordService.load(word.id) else word)!!.text)
            }
        }
        container.appendChild(wrapper.element)
        words.add(word to wrapper)
        Tooltips.getCurrent().setTooltip(wrapper, word.text)
    }

    private fun mouseDown(event: DomEvent, img: Element) {
        clearSelection()
        val x = event.eventData.getNumber("event.offsetX")
        val y = event.eventData.getNumber("event.offsetY")
        val btn = event.eventData.getNumber("event.button").toInt()
        drawing = !selecting && btn == 0
        selecting = !drawing && btn == 2
        mouseStart = System.currentTimeMillis()
        img.executeJs("return this.clientWidth").then { w ->
            img.executeJs("return this.clientHeight").then { h ->
                drawDiv.apply {
                    mouseXStart = (x / w.asNumber()) * 100.0
                    mouseYStart = (y / h.asNumber()) * 100.0
                }
            }
        }
    }

    private fun mouseMove(event: DomEvent, img: Element) {
        if (drawing || selecting) {
            val x = event.eventData.getNumber("event.offsetX")
            val y = event.eventData.getNumber("event.offsetY")
            img.executeJs("return this.clientWidth").then { w ->
                img.executeJs("return this.clientHeight").then { h ->
                    drawDiv.apply {
                        val xx = (x / w.asNumber()) * 100.0
                        val yy = (y / h.asNumber()) * 100.0
                        if (xx < mouseXStart) {
                            mouseWidth = mouseXStart - xx
                            mouseX = xx
                        } else {
                            mouseWidth = xx - mouseXStart
                            mouseX = mouseXStart
                        }
                        if (yy < mouseYStart) {
                            mouseHeight = mouseYStart - yy
                            mouseY = yy
                        } else {
                            mouseHeight = yy - mouseYStart
                            mouseY = mouseYStart
                        }
                        element.style["left"] = "$mouseX%"
                        element.style["top"] = "$mouseY%"
                        element.style["width"] = "$mouseWidth%"
                        element.style["height"] = "$mouseHeight%"
                        element.style.set("display", "block")
                    }
                }
            }
        }
    }

    private fun mouseUp() {
        clearSelection()
        if (System.currentTimeMillis() - mouseStart < 500) return
        drawDiv.element.style["display"] = "none"
        if (drawing) {
            val line = (doc?.let { lineService.findByDoc(it) } ?: lines).minBy { abs(it.y - mouseY) }!!
            val txt = docParser.getOcrTextRect(fileManager.getFirstImage((doc?.guid
                    ?: guid)!!), mouseX.toFloat(), mouseY.toFloat(), mouseWidth.toFloat(), mouseHeight.toFloat())
            val word = Word(line, txt, mouseX.toFloat(), mouseY.toFloat(), mouseWidth.toFloat(), mouseHeight.toFloat())
            line.words = line.words.plus(word)
            if (doc?.guid != null) {
                wordService.save(word)
            }
            addWordWrapper(word)
        } else if (selecting) {
            clearSelection()
            addSelection(words.filter { containedInSelection(it.first) })
        }
        drawing = false
        selecting = false
    }

    private fun clearSelection() {
        selected.forEach { it.second.removeClassName("word-wrapper-selected") }
        selected.clear()
        deleteButton.isEnabled = false
    }

    private fun addSelection(pairs: List<Pair<Word, Div>>) {
        pairs.forEach { it.second.addClassName("word-wrapper-selected") }
        selected.addAll(pairs)
        deleteButton.isEnabled = true
    }

    private fun containedInSelection(word: Word) = word.x >= mouseX && word.y >= mouseY
            && word.x + word.width <= mouseX + mouseWidth && word.y + word.height <= mouseY + mouseHeight
}