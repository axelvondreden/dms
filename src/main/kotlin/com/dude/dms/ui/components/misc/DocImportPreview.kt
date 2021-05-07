package com.dude.dms.ui.components.misc

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.utils.*
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import dev.mett.vaadin.tooltip.Tooltips
import java.util.*


class DocImportPreview : VerticalLayout() {

    private var docContainer: DocContainer? = null

    private lateinit var imageEditor: DocImageEditor

    private lateinit var editContainer: Div

    private lateinit var infoLayout: DocInfoLayout

    private lateinit var zoomButton: Button

    private lateinit var datePick: Button

    private lateinit var date: DatePicker

    private lateinit var pageSelector: DocPageSelector

    private lateinit var modeSelector: ModeSelector

    private lateinit var pdfButton: Button
    private lateinit var ocrButton: Button

    var onDone: ((DocContainer) -> Unit)? = null

    private lateinit var doneButton: Button

    init {
        setSizeFull()
        isPadding = false

        splitLayout {
            setSizeFull()
            setSecondaryStyle("minWidth", "250px")
            setSecondaryStyle("maxWidth", "400px")

            addToPrimary(Div().apply {
                setSizeFull()
                style["overflowY"] = "hidden"
                style["display"] = "flex"
                style["flexDirection"] = "column"

                horizontalLayout {
                    setWidthFull()

                    pageSelector = docPageSelector { }
                    horizontalLayout(isPadding = false, isSpacing = false) {
                        iconButton(VaadinIcon.MINUS_CIRCLE.create()) {
                            tooltip(t("zoom.out"))
                            onLeftClick { imageEditor.shrink(zoomButton) }
                        }
                        zoomButton = button("100%") {
                            tooltip(t("zoom.reset"))
                            onLeftClick { imageEditor.resetZoom(it.source) }
                            style["margin"] = "auto 5px"
                        }
                        iconButton(VaadinIcon.PLUS_CIRCLE.create()) {
                            tooltip(t("zoom.in"))
                            onLeftClick { imageEditor.grow(zoomButton) }
                        }
                    }
                    horizontalLayout(isPadding = false, isSpacing = false) {
                        pdfButton = button("PDF") {
                            onLeftClick {
                                docContainer?.let { dc ->
                                    dc.useOcrTxt = false
                                    imageEditor.fillWords(dc.pages.find { it.nr == pageSelector.page }!!, true)
                                    refreshTextTools(dc)
                                }
                            }
                        }
                        ocrButton = button("OCR") {
                            onLeftClick {
                                docContainer?.let { dc ->
                                    dc.useOcrTxt = true
                                    imageEditor.fillWords(dc.pages.find { it.nr == pageSelector.page }!!, true)
                                    refreshTextTools(dc)
                                }
                            }
                            style["marginLeft"] = "5px"
                        }
                    }
                    modeSelector = modeSelector { }
                    horizontalLayout(isPadding = false, isSpacing = false) {
                        date = datePicker {
                            tooltip(t("doc.date"))
                            addValueChangeListener { event -> event.value?.let { docContainer?.date = it } }
                            locale = Locale.forLanguageTag(Options.get().view.locale)
                        }
                        datePick = iconButton(VaadinIcon.CROSSHAIRS.create()) {
                            tooltip(t("doc.date.pick"))
                            onLeftClick { pickDate() }
                        }
                    }
                    doneButton = button(t("done")) {
                        tooltip(t("doc.done"))
                        onLeftClick { if (infoLayout.validate()) onDone?.invoke(docContainer!!) }
                        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                        style["margin"] = "auto"
                        style["marginRight"] = "4px"
                    }
                }
                editContainer = div {
                    setSizeFull()
                    style["overflowY"] = "auto"
                    style["flexGrow"] = "1"

                    imageEditor = docImageEditor {
                        onTextChange = { refreshTextTools(it) }
                    }
                }
            })
            infoLayout = DocInfoLayout(imageEditor)
            addToSecondary(infoLayout)
        }
    }

    private fun pickDate() {
        imageEditor.pickEvent = {
            date.value = it?.word?.text?.findDate() ?: date.value
            datePick.removeThemeVariants(ButtonVariant.LUMO_SUCCESS)
        }
        datePick.addThemeVariants(ButtonVariant.LUMO_SUCCESS)
    }

    fun fill(docContainer: DocContainer) {
        this.docContainer = docContainer
        pageSelector.setChangeListener { page -> imageEditor.fill(docContainer, docContainer.pages.find { it.nr == page }!!, true) }
        pageSelector.max = docContainer.pages.size
        pageSelector.page = 1
        modeSelector.setChangeListener { imageEditor.mode = it }
        infoLayout.fill(docContainer)
        date.value = docContainer.date

        refreshTextTools(docContainer)
    }

    fun clear() {
        docContainer = null
        date.clear()
        pageSelector.max = 1
        pdfButton.text = "PDF"
        ocrButton.text = "OCR"
        imageEditor.clear()
        infoLayout.clear()
    }

    private fun refreshTextTools(docContainer: DocContainer) {
        val pdfCount = docContainer.pdfPages.flatMap { it.lines }.sumOf { it.words.size }
        val ocrCount = docContainer.ocrPages.flatMap { it.lines }.sumOf { it.words.size }
        val pdfSpelling = ((1F - (docContainer.pdfPages.flatMap { it.lines }.flatMap { it.words }.count { it.spelling != null } / pdfCount.toFloat())) * 100.0F).toInt()
        val ocrSpelling = ((1F - (docContainer.ocrPages.flatMap { it.lines }.flatMap { it.words }.count { it.spelling != null } / ocrCount.toFloat())) * 100.0F).toInt()

        pdfButton.text = "PDF $pdfCount / $pdfSpelling %"
        Tooltips.getCurrent().removeTooltip(pdfButton)
        pdfButton.tooltip("$pdfCount ${t("words")}\n $pdfSpelling % ${t("spelling")}")
        ocrButton.text = "OCR $ocrCount / $ocrSpelling %"
        Tooltips.getCurrent().removeTooltip(ocrButton)
        ocrButton.tooltip("${t("language")}: ${docContainer.language}\n$ocrCount ${t("words")}\n $ocrSpelling % ${t("spelling")}")

        if (docContainer.useOcrTxt) {
            ocrButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            pdfButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY)
        } else {
            pdfButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            ocrButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }
    }
}