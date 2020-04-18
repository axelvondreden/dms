package com.dude.dms.ui.components.misc

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.service.DocService
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.splitlayout.SplitLayout
import dev.mett.vaadin.tooltip.Tooltips
import java.util.*


class DocImportPreview(builderFactory: BuilderFactory, private val docService: DocService) : VerticalLayout() {

    private var docContainer: DocContainer? = null

    private val imageEditor = builderFactory.docs().imageEditor().apply {
        onTextChange = { refreshTextTools(it) }
    }

    private val editContainer = Div(imageEditor).apply {
        setSizeFull()
        style["overflowY"] = "auto"
        style["flexGrow"] = "1"
    }

    private val attributeValueContainer = builderFactory.attributes().valueContainer().apply {
        setWidthFull()
        maxHeight = "50%"
        onChange = { doneButton.isEnabled = this.validate() }
    }

    private val tagSelector = builderFactory.tags().selector().apply {
        maxHeight = "50%"
        asMultiSelect().addSelectionListener { event ->
            docContainer?.tags = event.value
            docContainer?.let { attributeValueContainer.fill(it) }
        }
    }

    private val zoomButton = Button("100%") { imageEditor.resetZoom(it.source) }

    private val date = DatePicker().apply {
        addValueChangeListener { it.value?.let { date -> docContainer?.date = date } }
        locale = Locale.forLanguageTag(Options.get().view.locale)
    }

    private val pdfButton = Button("PDF") {
        docContainer?.let {
            it.useOcrTxt = false
            imageEditor.fillWords(it)
            refreshTextTools(it)
        }
    }
    private val ocrButton = Button("OCR") {
        docContainer?.let {
            it.useOcrTxt = true
            imageEditor.fillWords(it)
            refreshTextTools(it)
        }
    }

    var onDone: ((DocContainer) -> Unit)? = null

    private val doneButton = Button(t("done")) { onDone?.invoke(docContainer!!) }.apply {
        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        isEnabled = false
    }

    init {
        setSizeFull()
        isPadding = false

        val shrinkButton = Button(VaadinIcon.MINUS_CIRCLE.create()) { imageEditor.shrink(zoomButton) }
        val growButton = Button(VaadinIcon.PLUS_CIRCLE.create()) { imageEditor.grow(zoomButton) }
        val header = HorizontalLayout(shrinkButton, zoomButton, growButton, pdfButton, ocrButton, date, doneButton).apply {
            setWidthFull()
        }
        val imageWrapper = Div(header, editContainer).apply {
            setSizeFull()
            style["overflowY"] = "hidden"
            style["display"] = "flex"
            style["flexDirection"] = "column"
        }
        val infoWrapper = Div(tagSelector, attributeValueContainer).apply { setSizeFull() }
        val split = SplitLayout(imageWrapper, infoWrapper).apply {
            setSizeFull()
            setSecondaryStyle("minWidth", "250px")
            setSecondaryStyle("maxWidth", "400px")
        }
        add(split)
    }

    fun fill(docContainer: DocContainer) {
        this.docContainer = docContainer
        imageEditor.fill(docContainer)
        tagSelector.selectedTags = docContainer.tags
        tagSelector.rawText = docService.getFullTextMemory(docContainer.lineEntities)
        tagSelector.showContainedTags(true)
        attributeValueContainer.fill(docContainer)
        date.value = docContainer.date
        doneButton.isEnabled = attributeValueContainer.validate(true)

        refreshTextTools(docContainer)
    }

    fun clear() {
        docContainer = null
        date.clear()
        pdfButton.text = "PDF"
        ocrButton.text = "OCR"
        imageEditor.clear()
        tagSelector.selectedTags = emptySet()
        tagSelector.rawText = null
        attributeValueContainer.clear()
    }

    private fun refreshTextTools(docContainer: DocContainer) {
        val pdfCount = docContainer.pdfLines.sumBy { it.words.size }
        val ocrCount = docContainer.ocrLines.sumBy { it.words.size }
        val pdfSpelling = ((1F - (docContainer.pdfLines.flatMap { it.words }.count { it.spelling != null } / pdfCount.toFloat())) * 100.0F).toInt()
        val ocrSpelling = ((1F - (docContainer.ocrLines.flatMap { it.words }.count { it.spelling != null } / ocrCount.toFloat())) * 100.0F).toInt()

        pdfButton.text = "PDF $pdfCount / $pdfSpelling %"
        Tooltips.getCurrent().removeTooltip(pdfButton)
        Tooltips.getCurrent().setTooltip(pdfButton, "$pdfCount ${t("words")}\n $pdfSpelling % ${t("spelling")}")
        ocrButton.text = "OCR $ocrCount / $ocrSpelling %"
        Tooltips.getCurrent().removeTooltip(ocrButton)
        Tooltips.getCurrent().setTooltip(ocrButton, "$ocrCount ${t("words")}\n $ocrSpelling % ${t("spelling")}")

        if (docContainer.useOcrTxt) {
            ocrButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            pdfButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY)
        } else {
            pdfButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            ocrButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }
    }
}