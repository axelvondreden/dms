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

class DocTextImportCard(builderFactory: BuilderFactory, private val fileContainer: DocImportDialog.FileContainer) : Card() {

    private val label = TitleLabel(fileContainer.file.name).apply {
        setAlignSelf(FlexComponent.Alignment.CENTER)
        isPadding = false
        isSpacing = false
    }

    private val pdfCount = fileContainer.pdfText?.flatMap { it.words }?.size ?: 0
    private val ocrCount = fileContainer.ocrText?.flatMap { it.words }?.size ?: 0

    private val pdfCheck = Checkbox("${t("pdf.text")} ($pdfCount ${t("words")})", pdfCount > ocrCount).apply {
        isEnabled = pdfCount > 0
        element.style["paddingTop"] = "10px"
    }
    private val ocrCheck = Checkbox("${t("ocr.text")} ($ocrCount ${t("words")})", pdfCount <= ocrCount).apply {
        isEnabled = ocrCount > 0
        element.style["paddingTop"] = "10px"
    }

    private val textWrapper = VerticalLayout(
            HorizontalLayout(
                    pdfCheck,
                    Button(VaadinIcon.EYE.create()) {
                        if (!fileContainer.pdfText.isNullOrEmpty()) {
                            builderFactory.docs().imageDialog(null, fileContainer.guid, fileContainer.pdfText!!).open()
                        }
                    }.apply { addThemeVariants(ButtonVariant.LUMO_SMALL) }
            ).apply { isPadding = false; isSpacing = false },
            HorizontalLayout(
                    ocrCheck,
                    Button(VaadinIcon.EYE.create()) {
                        if (!fileContainer.ocrText.isNullOrEmpty()) {
                            builderFactory.docs().imageDialog(null, fileContainer.guid, fileContainer.ocrText!!).open()
                        }
                    }.apply { addThemeVariants(ButtonVariant.LUMO_SMALL) }
            ).apply { isPadding = false; isSpacing = false }
    ).apply { isPadding = false; isSpacing = false }

    init {
        setWidthFull()

        fileContainer.useOcrTxt = ocrCount > pdfCount

        pdfCheck.addValueChangeListener {
            if (!ocrCheck.isEnabled && it.isFromClient) it.source.value = it.oldValue
            else if (it.isFromClient) {
                fileContainer.useOcrTxt = !it.value
                ocrCheck.value = !ocrCheck.value
            }
        }
        ocrCheck.addValueChangeListener {
            if (!pdfCheck.isEnabled && it.isFromClient) it.source.value = it.oldValue
            else if (it.isFromClient) {
                fileContainer.useOcrTxt = it.value
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