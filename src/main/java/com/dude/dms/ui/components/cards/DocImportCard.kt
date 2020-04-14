package com.dude.dms.ui.components.cards

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.github.appreciated.card.Card
import com.github.appreciated.card.label.TitleLabel
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import java.util.*

class DocImportCard(
        builderFactory: BuilderFactory,
        private val tagService: TagService,
        private val docContainer: DocContainer
) : Card() {

    private val label = TitleLabel(docContainer.file?.name).apply {
        setAlignSelf(FlexComponent.Alignment.CENTER)
    }

    private val date = DatePicker().apply {
        docContainer.date?.let { value = it }
        addValueChangeListener { it.value?.let { date -> docContainer.date = date } }
        locale = Locale.forLanguageTag(Options.get().view.locale)
        element.style["padding"] = "10px"
    }

    private val pdfCount
        get() = docContainer.pdfLines.sumBy { it.words.size }
    private val ocrCount
        get() = docContainer.ocrLines.sumBy { it.words.size }
    private val pdfSpelling
        get() = (1F - (docContainer.pdfLines.flatMap { it.words }.count { !it.spelling.isNullOrEmpty() } / pdfCount)) * 100.0F
    private val ocrSpelling
        get() = (1F - (docContainer.ocrLines.flatMap { it.words }.count { !it.spelling.isNullOrEmpty() } / ocrCount)) * 100.0F

    private val pdfCheckLabel
        get() = "${t("pdf.text")} ($pdfCount ${t("words")}, $pdfSpelling % ${t("spelling")})"
    private val ocrCheckLabel
        get() = "${t("ocr.text")} ($ocrCount ${t("words")}, $ocrSpelling % ${t("spelling")})"

    private val pdfCheck = Checkbox(pdfCheckLabel, pdfCount > ocrCount).apply {
        isEnabled = pdfCount > 0
        element.style["paddingTop"] = "10px"
    }
    private val ocrCheck = Checkbox(ocrCheckLabel, pdfCount <= ocrCount).apply {
        isEnabled = ocrCount > 0
        element.style["paddingTop"] = "10px"
    }

    private val pdfDlg = builderFactory.docs().imageDialog(docContainer).apply {
        addOpenedChangeListener {
            if (!it.isOpened) {
                pdfCheck.label = pdfCheckLabel
            }
        }
    }

    private val ocrDlg = builderFactory.docs().imageDialog(docContainer).apply {
        addOpenedChangeListener {
            if (!it.isOpened) {
                ocrCheck.label = ocrCheckLabel
            }
        }
    }

    private val textWrapper = VerticalLayout(
            HorizontalLayout(
                    pdfCheck,
                    Button(VaadinIcon.EYE.create()) {
                        if (!docContainer.pdfLines.isNullOrEmpty()) {
                            pdfDlg.open()
                        }
                    }.apply { addThemeVariants(ButtonVariant.LUMO_SMALL) }
            ).apply { isPadding = false; isSpacing = false },
            HorizontalLayout(
                    ocrCheck,
                    Button(VaadinIcon.EYE.create()) {
                        if (!docContainer.ocrLines.isNullOrEmpty()) {
                            ocrDlg.open()
                        }
                    }.apply { addThemeVariants(ButtonVariant.LUMO_SMALL) }
            ).apply { isPadding = false; isSpacing = false }
    ).apply { isPadding = false; isSpacing = false }

    private val tagContainer = builderFactory.tags().container(docContainer.tags, true) { event ->
        event.source.children.findFirst().ifPresent { elem -> docContainer.tags.remove(tagService.findByName(elem.element.text!!)) }
        fill()
    }.apply { setWidthFull() }

    init {
        pdfCheck.addValueChangeListener {
            if (!ocrCheck.isEnabled && it.isFromClient) it.source.value = it.oldValue
            else if (it.isFromClient) {
                docContainer.useOcrTxt = !it.value
                ocrCheck.value = !ocrCheck.value
            }
        }
        ocrCheck.addValueChangeListener {
            if (!pdfCheck.isEnabled && it.isFromClient) it.source.value = it.oldValue
            else if (it.isFromClient) {
                docContainer.useOcrTxt = it.value
                pdfCheck.value = !pdfCheck.value
            }
        }

        fill()
    }

    private fun fill() {
        tagContainer.setTags(docContainer.tags)
        val wrapper = VerticalLayout(
                HorizontalLayout(label, date).apply { setWidthFull(); alignItems = FlexComponent.Alignment.CENTER },
                textWrapper,
                tagContainer
        ).apply {
            setWidthFull()
            alignItems = FlexComponent.Alignment.CENTER
            isPadding = false
            isSpacing = false
        }
        add(wrapper)
    }

    fun resize() {
        val size = Options.get().view.docCardSize
        width = "${size * 2}px"
        height = "${size}px"
    }
}