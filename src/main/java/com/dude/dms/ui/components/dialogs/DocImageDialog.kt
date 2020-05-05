package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.service.DocService
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.extensions.findDate
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.misc.DocPageSelector
import com.dude.dms.ui.components.misc.ModeSelector
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.splitlayout.SplitLayout
import dev.mett.vaadin.tooltip.Tooltips
import java.util.*

class DocImageDialog(builderFactory: BuilderFactory, private val docService: DocService) : DmsDialog(t("doc.details")) {

    private var docContainer: DocContainer? = null

    private val imageEditor = builderFactory.docs().imageEditor()

    private val infoLayout = builderFactory.docs().infoLayout(imageEditor)

    private val editContainer = Div(imageEditor).apply {
        maxWidth = "80vw"
        maxHeight = "80vh"
        style["overflowY"] = "auto"
    }

    private val zoomButton = Button("100%") { imageEditor.resetZoom(it.source) }

    private val pageSelector = DocPageSelector()

    private val modeSelector = ModeSelector().apply {
        setChangeListener { imageEditor.mode = it }
    }

    private val datePick = Button(VaadinIcon.CROSSHAIRS.create()) { pickDate() }

    private val date = DatePicker().apply {
        addValueChangeListener { event -> event.value?.let { docContainer?.date = it } }
        locale = Locale.forLanguageTag(Options.get().view.locale)
    }

    init {
        val shrinkButton = Button(VaadinIcon.MINUS_CIRCLE.create()) { imageEditor.shrink(zoomButton) }
        val growButton = Button(VaadinIcon.PLUS_CIRCLE.create()) { imageEditor.grow(zoomButton) }
        val wordsButton = Button(VaadinIcon.AREA_SELECT.create()) {
            val options = Options.get()
            options.view.loadWordsInPreview = !Options.get().view.loadWordsInPreview
            options.save()
            it.source.removeThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SUCCESS)
            it.source.addThemeVariants(if (Options.get().view.loadWordsInPreview) ButtonVariant.LUMO_SUCCESS else ButtonVariant.LUMO_ERROR)
            pageSelector.page = pageSelector.page
        }.apply {
            Tooltips.getCurrent().setTooltip(this, t("words.preview.show"))
            addThemeVariants(if (Options.get().view.loadWordsInPreview) ButtonVariant.LUMO_SUCCESS else ButtonVariant.LUMO_ERROR)
        }
        val horizontalLayout = HorizontalLayout(
                pageSelector,
                modeSelector,
                HorizontalLayout(date, datePick).apply { isSpacing = false; isPadding = false },
                shrinkButton,
                zoomButton,
                growButton,
                wordsButton
        ).apply {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        }
        val split = SplitLayout(editContainer, infoLayout).apply {
            setSizeFull()
            setSecondaryStyle("minWidth", "200px")
            setSecondaryStyle("maxWidth", "300px")
        }
        add(horizontalLayout, split)
        addOpenedChangeListener {
            if (!it.isOpened) {
                docContainer?.doc?.let(docService::save)
            }
        }
    }

    fun fill(docContainer: DocContainer) {
        infoLayout.fill(docContainer)
        pageSelector.setChangeListener { page -> imageEditor.fill(docContainer, docContainer.pages.find { it.nr == page }!!) }
        pageSelector.page = 1
        date.value = docContainer.date
    }

    private fun pickDate() {
        imageEditor.pickEvent = {
            date.value = it?.word?.text?.findDate() ?: date.value
            datePick.removeThemeVariants(ButtonVariant.LUMO_SUCCESS)
        }
        datePick.addThemeVariants(ButtonVariant.LUMO_SUCCESS)
    }
}