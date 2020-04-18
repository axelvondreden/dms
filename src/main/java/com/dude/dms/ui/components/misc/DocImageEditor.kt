package com.dude.dms.ui.components.misc

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.LineContainer
import com.dude.dms.backend.containers.WordContainer
import com.dude.dms.backend.data.docs.Word
import com.dude.dms.backend.service.LineService
import com.dude.dms.backend.service.WordService
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.brain.parsing.Spellchecker
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.helger.commons.io.file.FileHelper
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.dom.DomEvent
import com.vaadin.flow.dom.Element
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import dev.mett.vaadin.tooltip.Tooltips
import kotlin.math.abs
import kotlin.streams.toList


class DocImageEditor(
        private val builderFactory: BuilderFactory,
        private val lineService: LineService,
        private val wordService: WordService,
        private val docParser: DocParser,
        private val fileManager: FileManager
) : Div() {

    private var zoom = 100

    private val drawDiv = Div().apply {
        addClassName("draw-div")
    }

    private val words = mutableSetOf<Pair<WordContainer, Div>>()

    private val selected = mutableSetOf<Pair<WordContainer, Div>>()

    private var drawing = false
    private var selecting = false
    private var mouseStart = 0L
    private var mouseXStart = 0.0
    private var mouseYStart = 0.0
    private var mouseX = 0.0
    private var mouseY = 0.0
    private var mouseWidth = 0.0
    private var mouseHeight = 0.0

    private var docContainer: DocContainer? = null

    var onTextChange: ((DocContainer) -> Unit)? = null

    init {
        element.classList.add("image-container")
    }

    fun fill(docContainer: DocContainer) {
        this.docContainer = docContainer
        element.removeAllChildren()
        words.clear()
        selected.clear()
        val img = fileManager.getFirstImage(docContainer.guid)
        val image = Element("img").apply {
            classList.add("inline-image")
            setAttribute("src", StreamResource("image.png", InputStreamFactory { FileHelper.getInputStream(img) }))
            setAttribute("ondragstart", "return false;")
            setAttribute("oncontextmenu", "return false;")
            addEventListener("mousedown") { mouseDown(it, this) }
                    .addEventData("event.offsetX").addEventData("event.offsetY").addEventData("event.button")
            addEventListener("mousemove") { mouseMove(it, this) }
                    .addEventData("event.offsetX").addEventData("event.offsetY")
            addEventListener("mouseup") { mouseUp() }
        }
        element.appendChild(image)
        fillWords(docContainer)
    }

    fun clear() {
        docContainer = null
        element.removeAllChildren()
        words.clear()
        selected.clear()
    }

    fun fillWords(docContainer: DocContainer) {
        val old = element.children.filter { it.tag == "div" }.toList()
        old.forEach { element.removeChild(it) }
        docContainer.lines.flatMap { it.words }.forEach { addWordWrapper(it) }
        element.appendChild(drawDiv.element)
    }

    private fun addWordWrapper(wordContainer: WordContainer) {
        val dlg = builderFactory.docs().wordEditDialog(wordContainer)
        val word = wordContainer.word
        val div = Div().apply {
            addClassName("word-container")
            element.setAttribute("oncontextmenu", "return false;")
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
            if (wordContainer.spelling != null) {
                addClassName("word-wrapper-error")
            }
            element.style["top"] = "${word.y}%"
            element.style["left"] = "${word.x}%"
            element.style["width"] = "${word.width}%"
            element.style["height"] = "${word.height}%"
        }
        delBtn.addClickListener {
            wordService.delete(word)
            docContainer?.lines?.forEach { it.words = it.words.minus(wordContainer) }
            element.removeChild(wrapper.element)
            onTextChange?.invoke(docContainer!!)
        }
        ocrBtn.addClickListener {
            word.text = docParser.getOcrTextRect(fileManager.getFirstImage(docContainer!!.guid), word.x, word.y, word.width, word.height)
            word.line?.let { line -> lineService.save(line) }
            wordService.save(word)
            Tooltips.getCurrent().setTooltip(wrapper, word.text)
            onTextChange?.invoke(docContainer!!)
        }
        dlg.addOpenedChangeListener {
            if (!it.isOpened) {
                Tooltips.getCurrent().setTooltip(wrapper, (if (word.id > 0) wordService.load(word.id) else word)!!.text)
                wordContainer.spelling = Spellchecker(docContainer!!.language).check(wordContainer.word.text)
                if (wordContainer.spelling != null) {
                    wrapper.addClassName("word-wrapper-error")
                } else {
                    wrapper.removeClassName("word-wrapper-error")
                }
                onTextChange?.invoke(docContainer!!)
            }
        }
        element.appendChild(wrapper.element)
        words.add(wordContainer to wrapper)
        Tooltips.getCurrent().setTooltip(wrapper, word.text)
    }

    fun resetZoom(zoomButton: Button) {
        zoom = 100
        zoom(zoomButton)
    }

    fun grow(zoomButton: Button) {
        if (zoom < 1000) zoom += 20
        zoom(zoomButton)
    }

    fun shrink(zoomButton: Button) {
        if (zoom > 40) zoom -= 20
        zoom(zoomButton)
    }

    private fun zoom(zoomButton: Button) {
        element.style["width"] = "$zoom%"
        zoomButton.text = "$zoom%"
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
        drawDiv.element.style["display"] = "none"
        if (System.currentTimeMillis() - mouseStart >= 500) {
            if (drawing) {
                val line = if (docContainer!!.inDB) {
                    LineContainer(docContainer!!.doc!!.lines.minBy { abs(it.y - mouseY) }!!)
                } else {
                    docContainer!!.lines.minBy { abs(it.y - mouseY) }!!
                }
                val txt = docParser.getOcrTextRect(fileManager.getFirstImage(docContainer!!.guid), mouseX.toFloat(), mouseY.toFloat(), mouseWidth.toFloat(), mouseHeight.toFloat())
                val wordContainer = WordContainer(Word(line.line, txt, mouseX.toFloat(), mouseY.toFloat(), mouseWidth.toFloat(), mouseHeight.toFloat()))
                line.words = line.words.plus(wordContainer)
                wordContainer.spelling = Spellchecker(docContainer!!.language).check(txt)
                docContainer!!.lines = docContainer!!.lines.minus(line).plus(line)
                if (docContainer!!.inDB) {
                    lineService.save(line.line)
                    wordService.create(wordContainer.word)
                }
                addWordWrapper(wordContainer)
            } else if (selecting) {
                clearSelection()
                addSelection(words.filter { containedInSelection(it.first) })
            }
            onTextChange?.invoke(docContainer!!)
        }
        drawing = false
        selecting = false
    }

    private fun clearSelection() {
        selected.forEach { it.second.removeClassName("word-wrapper-selected") }
        selected.clear()
    }

    private fun addSelection(pairs: List<Pair<WordContainer, Div>>) {
        pairs.forEach { it.second.addClassName("word-wrapper-selected") }
        selected.addAll(pairs)
    }

    private fun containedInSelection(wordContainer: WordContainer): Boolean {
        val word = wordContainer.word
        return (word.x >= mouseX && word.y >= mouseY && word.x + word.width <= mouseX + mouseWidth && word.y + word.height <= mouseY + mouseHeight)
    }
}