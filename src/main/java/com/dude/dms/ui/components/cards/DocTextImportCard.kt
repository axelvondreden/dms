package com.dude.dms.ui.components.cards

import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.dialogs.docimport.DocImportDialog
import com.github.appreciated.card.Card
import com.github.appreciated.card.label.TitleLabel
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import kotlin.math.roundToInt

class DocTextImportCard(builderFactory: BuilderFactory, private val docContainer: DocImportDialog.DocContainer) : Card() {

    private val label = TitleLabel(docContainer.file.name).apply {
        setAlignSelf(FlexComponent.Alignment.CENTER)
        isPadding = false
        isSpacing = false
    }

    private val pdfCount
        get() = docContainer.pdfText?.flatMap { it.words }?.size ?: 0
    private val ocrCount
        get() = docContainer.ocrText?.flatMap { it.words }?.size ?: 0

    private val pdfCheck = Checkbox("${t("pdf.text")} ($pdfCount ${t("words")}, ${docContainer.pdfSpelling.roundToInt()} % ${t("spelling")})", pdfCount > ocrCount).apply {
        isEnabled = pdfCount > 0
        element.style["paddingTop"] = "10px"
    }
    private val ocrCheck = Checkbox("${t("ocr.text")} ($ocrCount ${t("words")}, ${docContainer.ocrSpelling.roundToInt()} % ${t("spelling")})", pdfCount <= ocrCount).apply {
        isEnabled = ocrCount > 0
        element.style["paddingTop"] = "10px"
    }

    private val pdfDlg = builderFactory.docs().imageDialog(null, docContainer.guid, docContainer.pdfText!!).apply {
        addOpenedChangeListener {
            if (!it.isOpened) {
                pdfCheck.label = "${t("pdf.text")} ($pdfCount ${t("words")})"
            }
        }
    }

    private val ocrDlg = builderFactory.docs().imageDialog(null, docContainer.guid, docContainer.ocrText!!).apply {
        addOpenedChangeListener {
            if (!it.isOpened) {
                ocrCheck.label = "${t("ocr.text")} ($ocrCount ${t("words")})"
            }
        }
    }

    private val textWrapper = VerticalLayout(
            HorizontalLayout(
                    pdfCheck,
                    Button(VaadinIcon.EYE.create()) {
                        if (!docContainer.pdfText.isNullOrEmpty()) {
                            pdfDlg.open()
                        }
                    }.apply { addThemeVariants(ButtonVariant.LUMO_SMALL) }
            ).apply { isPadding = false; isSpacing = false },
            HorizontalLayout(
                    ocrCheck,
                    Button(VaadinIcon.EYE.create()) {
                        if (!docContainer.ocrText.isNullOrEmpty()) {
                            ocrDlg.open()
                        }
                    }.apply { addThemeVariants(ButtonVariant.LUMO_SMALL) }
            ).apply { isPadding = false; isSpacing = false }
    ).apply { isPadding = false; isSpacing = false }

    init {
        setWidthFull()

        docContainer.useOcrTxt = ocrCount > pdfCount

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

        val wrapper = HorizontalLayout(label, textWrapper).apply {
            setWidthFull()
            alignItems = FlexComponent.Alignment.CENTER
            isPadding = true
        }
        add(wrapper)
    }
}