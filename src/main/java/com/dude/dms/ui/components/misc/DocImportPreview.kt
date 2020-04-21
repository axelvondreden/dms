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

    private val pageSelector = PageSelector()

    private val pdfButton = Button("PDF") {
        docContainer?.let { dc ->
            dc.useOcrTxt = false
            imageEditor.fillWords(dc.pages.find { it.nr == pageSelector.page }!!)
            refreshTextTools(dc)
        }
    }
    private val ocrButton = Button("OCR") {
        docContainer?.let { dc ->
            dc.useOcrTxt = true
            imageEditor.fillWords(dc.pages.find { it.nr == pageSelector.page }!!)
            refreshTextTools(dc)
        }
    }

    var onDone: ((DocContainer) -> Unit)? = null

    private val doneButton = Button(t("done")) { onDone?.invoke(docContainer!!) }.apply {
        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        isEnabled = false
        style["margin"] = "auto"
        style["marginRight"] = "0px"
    }

    init {
        setSizeFull()
        isPadding = false

        val shrinkButton = Button(VaadinIcon.MINUS_CIRCLE.create()) { imageEditor.shrink(zoomButton) }
        val growButton = Button(VaadinIcon.PLUS_CIRCLE.create()) { imageEditor.grow(zoomButton) }
        val header = HorizontalLayout(pageSelector, shrinkButton, zoomButton, growButton, pdfButton, ocrButton, date, doneButton).apply {
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
        pageSelector.max = docContainer.pages.size
        pageSelector.page = 1
        pageSelector.setChangeListener { page -> imageEditor.fill(docContainer, docContainer.pages.find { it.nr == page}!!) }
        imageEditor.fill(docContainer, docContainer.pages.find { it.nr == 1 }!!)
        tagSelector.selectedTags = docContainer.tags
        tagSelector.rawText = docService.getFullText(docContainer.pageEntities)
        tagSelector.showContainedTags(true)
        attributeValueContainer.fill(docContainer)
        date.value = docContainer.date
        doneButton.isEnabled = attributeValueContainer.validate(true)

        refreshTextTools(docContainer)
    }

    fun clear() {
        docContainer = null
        date.clear()
        pageSelector.page = 1
        pageSelector.max = 1
        pdfButton.text = "PDF"
        ocrButton.text = "OCR"
        imageEditor.clear()
        tagSelector.selectedTags = emptySet()
        tagSelector.rawText = null
        attributeValueContainer.clear()
    }

    private fun refreshTextTools(docContainer: DocContainer) {
        val pdfCount = docContainer.pdfPages.flatMap { it.lines }.sumBy { it.words.size }
        val ocrCount = docContainer.ocrPages.flatMap { it.lines }.sumBy { it.words.size }
        val pdfSpelling = ((1F - (docContainer.pdfPages.flatMap { it.lines }.flatMap { it.words }.count { it.spelling != null } / pdfCount.toFloat())) * 100.0F).toInt()
        val ocrSpelling = ((1F - (docContainer.ocrPages.flatMap { it.lines }.flatMap { it.words }.count { it.spelling != null } / ocrCount.toFloat())) * 100.0F).toInt()

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