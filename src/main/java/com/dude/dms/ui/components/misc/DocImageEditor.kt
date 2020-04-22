package com.dude.dms.ui.components.misc

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.LineContainer
import com.dude.dms.backend.containers.PageContainer
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
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.progressbar.ProgressBar
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

    private var pageContainer: PageContainer? = null

    private val progress = ProgressBar().apply {
        setWidthFull()
        isVisible = false
        style["position"] = "absolute"
    }

    var onTextChange: ((DocContainer) -> Unit)? = null

    init {
        element.classList.add("image-container")
    }

    fun fill(docContainer: DocContainer, pageContainer: PageContainer) {
        this.docContainer = docContainer
        this.pageContainer = pageContainer
        element.removeAllChildren()
        words.clear()
        selected.clear()
        val img = fileManager.getImage(docContainer.guid, pageContainer.nr)
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
        element.appendChild(progress.element, image)
        fillWords(pageContainer)
    }

    fun clear() {
        docContainer = null
        pageContainer = null
        element.removeAllChildren()
        words.clear()
        selected.clear()
    }

    fun fillWords(pageContainer: PageContainer) {
        val old = element.children.filter { it.tag == "div" }.toList()
        old.forEach { element.removeChild(it) }
        val words = pageContainer.lines.flatMap { it.words }
        progress.isVisible = true
        progress.max = words.size.toDouble()
        val ui = UI.getCurrent()
        Thread {
            words.chunked(10).withIndex().forEach {
                addWordWrappers(it.value.toSet(), ui)
                ui.access { progress.value = it.index.toDouble() * 10 }
            }
            ui.access {
                element.appendChild(drawDiv.element)
                progress.isVisible = false
            }
        }.start()
    }

    private fun addWordWrappers(wordContainers: Set<WordContainer>, ui: UI? = null) {
        val data = mutableSetOf<WordWrapperData>()
        wordContainers.forEach { wordContainer ->
            val dlg = builderFactory.docs().wordEditDialog(wordContainer)
            val word = wordContainer.word
            val div = Div().apply {
                addClassName("word-container")
                element.setAttribute("oncontextmenu", "return false;")
                element.addEventListener("click") { dlg.open() }
            }
            val delBtn = Button(VaadinIcon.TRASH.create()).apply {
                //addThemeVariants(ButtonVariant.LUMO_CONTRAST)
                addClassName("word-dropdown-button")
            }
            val ocrBtn = Button(VaadinIcon.CROSSHAIRS.create()).apply {
                //addThemeVariants(ButtonVariant.LUMO_CONTRAST)
                addClassName("word-dropdown-button")
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
                pageContainer?.lines?.forEach { it.words = it.words.minus(wordContainer) }
                if (ui != null) ui.access { element.removeChild(wrapper.element) } else element.removeChild(wrapper.element)
                onTextChange?.invoke(docContainer!!)
            }
            ocrBtn.addClickListener {
                word.text = docParser.getOcrTextRect(pageContainer!!.image!!, word.x, word.y, word.width, word.height)
                wordService.save(word)
                if (ui != null) ui.access { Tooltips.getCurrent().setTooltip(wrapper, word.text) } else Tooltips.getCurrent().setTooltip(wrapper, word.text)
                onTextChange?.invoke(docContainer!!)
            }
            dlg.addOpenedChangeListener {
                if (!it.isOpened) {
                    if (ui != null) ui.access { Tooltips.getCurrent().setTooltip(wrapper, (if (word.id > 0) wordService.load(word.id) else word)!!.text) }
                    else Tooltips.getCurrent().setTooltip(wrapper, (if (word.id > 0) wordService.load(word.id) else word)!!.text)
                    wordContainer.spelling = Spellchecker(docContainer!!.language).check(wordContainer.word.text)
                    if (wordContainer.spelling != null) {
                        wrapper.addClassName("word-wrapper-error")
                    } else {
                        wrapper.removeClassName("word-wrapper-error")
                    }
                    onTextChange?.invoke(docContainer!!)
                }
            }
            words.add(wordContainer to wrapper)
            data.add(WordWrapperData(wrapper, word, delBtn, ocrBtn))
        }

        if (ui != null) ui.access { addWrappersToView(data) } else addWrappersToView(data)
    }

    private fun addWrappersToView(wrappers: Set<WordWrapperData>) {
        element.appendChild(*wrappers.map { it.wrapper.element }.toTypedArray())
        wrappers.forEach {
            Tooltips.getCurrent().apply {
                setTooltip(it.wrapper, it.word.text)
                setTooltip(it.delBtn, t("delete"))
                setTooltip(it.ocrBtn, t("ocr.run"))
            }
        }
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
                    LineContainer(pageContainer!!.page.lines.minBy { abs(it.y - mouseY) }!!)
                } else {
                    pageContainer!!.lines.minBy { abs(it.y - mouseY) }!!
                }
                val txt = docParser.getOcrTextRect(fileManager.getImage(docContainer!!.guid, pageContainer!!.nr), mouseX.toFloat(), mouseY.toFloat(), mouseWidth.toFloat(), mouseHeight.toFloat())
                val wordContainer = WordContainer(Word(line.line, txt, mouseX.toFloat(), mouseY.toFloat(), mouseWidth.toFloat(), mouseHeight.toFloat()))
                line.words = line.words.plus(wordContainer)
                wordContainer.spelling = Spellchecker(docContainer!!.language).check(txt)
                pageContainer!!.lines = pageContainer!!.lines.minus(line).plus(line)
                if (docContainer!!.inDB) {
                    wordService.create(wordContainer.word)
                    lineService.save(line.line)
                }
                addWordWrappers(setOf(wordContainer))
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

    data class WordWrapperData(val wrapper: Div, val word: Word, val delBtn: Button, val ocrBtn: Button)
}