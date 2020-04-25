package com.dude.dms.ui.components.misc

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.service.DocService
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.extensions.findDate
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

    private val attributeValueContainer = builderFactory.attributes().valueContainer(false, imageEditor).apply {
        setWidthFull()
        maxHeight = "50%"
    }

    private val tagSelector = builderFactory.tags().selector().apply {
        maxHeight = "50%"
        asMultiSelect().addSelectionListener { event ->
            docContainer?.tags = event.value
            docContainer?.let { attributeValueContainer.fill(it) }
        }
    }

    private val zoomButton = Button("100%") { imageEditor.resetZoom(it.source) }.apply { style["margin"] = "auto 5px" }

    private val datePick = Button(VaadinIcon.CROSSHAIRS.create()) { pickDate() }

    private val date = DatePicker().apply {
        addValueChangeListener { event -> event.value?.let { docContainer?.date = it } }
        locale = Locale.forLanguageTag(Options.get().view.locale)
    }

    private val pageSelector = PageSelector()

    private val modeSelector = ModeSelector()

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

    private val doneButton = Button(t("done")) { if (attributeValueContainer.validate()) onDone?.invoke(docContainer!!) }.apply {
        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        style["margin"] = "auto"
        style["marginRight"] = "4px"
    }

    init {
        setSizeFull()
        isPadding = false

        val header = HorizontalLayout(
                pageSelector,
                HorizontalLayout(
                        Button(VaadinIcon.MINUS_CIRCLE.create()) { imageEditor.shrink(zoomButton) },
                        zoomButton,
                        Button(VaadinIcon.PLUS_CIRCLE.create()) { imageEditor.grow(zoomButton) }
                ).apply { isSpacing = false; isPadding = false },
                HorizontalLayout(pdfButton, ocrButton).apply { isSpacing = false; isPadding = false },
                modeSelector,
                HorizontalLayout(date, datePick).apply { isSpacing = false; isPadding = false },
                doneButton
        ).apply { setWidthFull() }
        ocrButton.style["marginLeft"] = "5px"
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

    private fun pickDate() {
        imageEditor.pickEvent = {
            date.value = it?.word?.text?.findDate() ?: date.value
            datePick.removeThemeVariants(ButtonVariant.LUMO_SUCCESS)
        }
        datePick.addThemeVariants(ButtonVariant.LUMO_SUCCESS)
    }

    fun fill(docContainer: DocContainer) {
        this.docContainer = docContainer
        pageSelector.setChangeListener { page -> imageEditor.fill(docContainer, docContainer.pages.find { it.nr == page }!!) }
        pageSelector.max = docContainer.pages.size
        pageSelector.page = 1
        modeSelector.setChangeListener { imageEditor.mode = it }
        tagSelector.selectedTags = docContainer.tags
        tagSelector.rawText = docService.getFullText(docContainer.pageEntities)
        tagSelector.showContainedTags(true)
        attributeValueContainer.fill(docContainer)
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