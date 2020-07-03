package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.LineService
import com.dude.dms.backend.service.WordService
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.brain.t
import com.dude.dms.extensions.findDate
import com.dude.dms.ui.components.misc.DocImageEditor
import com.dude.dms.ui.components.misc.DocInfoLayout
import com.dude.dms.ui.components.misc.DocPageSelector
import com.dude.dms.ui.components.misc.ModeSelector
import com.dude.dms.ui.docImageEditor
import com.dude.dms.ui.docInfoLayout
import com.dude.dms.ui.modeSelector
import com.dude.dms.ui.pageSelector
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import dev.mett.vaadin.tooltip.Tooltips
import java.util.*

class DocImageDialog(
        private val docService: DocService,
        lineService: LineService,
        wordService: WordService,
        docParser: DocParser,
        fileManager: FileManager
) : DmsDialog(t("doc.details")) {

    private var docContainer: DocContainer? = null

    private lateinit var imageEditor: DocImageEditor

    private lateinit var infoLayout: DocInfoLayout

    private lateinit var editContainer: Div

    private lateinit var zoomButton: Button

    private lateinit var pageSelector: DocPageSelector

    private lateinit var modeSelector: ModeSelector

    private lateinit var datePick: Button

    private lateinit var date: DatePicker

    init {
        horizontalLayout {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER

            pageSelector = pageSelector {  }
            modeSelector = modeSelector { setChangeListener { imageEditor.mode = it } }
            horizontalLayout(isPadding = false, isSpacing = false) {
                date = datePicker {
                    addValueChangeListener { event -> event.value?.let { docContainer?.date = it } }
                    locale = Locale.forLanguageTag(Options.get().view.locale)
                }
                datePick = iconButton(VaadinIcon.CROSSHAIRS.create()) {
                    onLeftClick { pickDate() }
                }
            }
            iconButton(VaadinIcon.MINUS_CIRCLE.create()) {
                onLeftClick { imageEditor.shrink(zoomButton) }
            }
            zoomButton = button("100%") {
                onLeftClick { imageEditor.resetZoom(it.source) }
            }
            iconButton(VaadinIcon.PLUS_CIRCLE.create()) {
                onLeftClick { imageEditor.grow(zoomButton) }
            }
            iconButton(VaadinIcon.AREA_SELECT.create()) {
                Tooltips.getCurrent().setTooltip(this, t("words.preview.show"))
                addThemeVariants(if (Options.get().view.loadWordsInPreview) ButtonVariant.LUMO_SUCCESS else ButtonVariant.LUMO_ERROR)
                onLeftClick {
                    val options = Options.get()
                    options.view.loadWordsInPreview = !Options.get().view.loadWordsInPreview
                    options.save()
                    it.source.removeThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SUCCESS)
                    it.source.addThemeVariants(if (Options.get().view.loadWordsInPreview) ButtonVariant.LUMO_SUCCESS else ButtonVariant.LUMO_ERROR)
                    pageSelector.page = pageSelector.page
                }
            }
        }
        splitLayout {
            setSizeFull()
            setSecondaryStyle("minWidth", "200px")
            setSecondaryStyle("maxWidth", "300px")

            editContainer = div {
                maxWidth = "80vw"
                maxHeight = "80vh"
                style["overflowY"] = "auto"

                imageEditor = docImageEditor(lineService, wordService, docParser, fileManager)
            }
            infoLayout = docInfoLayout(imageEditor)
        }

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