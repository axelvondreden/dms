package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.extensions.*
import com.dude.dms.ui.components.misc.DocImageEditor
import com.dude.dms.ui.components.misc.DocInfoLayout
import com.dude.dms.ui.components.misc.DocPageSelector
import com.dude.dms.ui.components.misc.ModeSelector
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import java.util.*

class DocImageDialog(private val docContainer: DocContainer) : DmsDialog(t("doc.details")) {

    private lateinit var imageEditor: DocImageEditor

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

            pageSelector = docPageSelector {
                setChangeListener { page -> imageEditor.fill(docContainer, docContainer.pages.find { it.nr == page }!!) }
            }
            modeSelector = modeSelector { setChangeListener { imageEditor.mode = it } }
            horizontalLayout(isPadding = false, isSpacing = false) {
                date = datePicker {
                    value = docContainer.date
                    addValueChangeListener { event -> event.value?.let { docContainer.date = it } }
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
                tooltip(t("words.preview.show"))
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

            editContainer = Div().apply {
                maxWidth = "80vw"
                maxHeight = "80vh"
                style["overflowY"] = "auto"

                imageEditor = docImageEditor()
            }
            addToPrimary(editContainer)
            addToSecondary(DocInfoLayout(imageEditor).apply { fill(docContainer) })
        }

        addOpenedChangeListener {
            if (!it.isOpened) {
                docContainer.doc?.let(docService::save)
            }
        }

        pageSelector.page = 1
    }

    private fun pickDate() {
        imageEditor.pickEvent = {
            date.value = it?.word?.text?.findDate() ?: date.value
            datePick.removeThemeVariants(ButtonVariant.LUMO_SUCCESS)
        }
        datePick.addThemeVariants(ButtonVariant.LUMO_SUCCESS)
    }
}