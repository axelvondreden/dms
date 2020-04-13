package com.dude.dms.ui.views

import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.DocImportService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.Const
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.cards.DocCard
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.progressbar.ProgressBar
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route


@Route(value = Const.PAGE_DOCIMPORT, layout = MainView::class)
@PageTitle("Doc Import")
class DocImportView(
        private val builderFactory: BuilderFactory,
        private val docImportService: DocImportService,
        private val tagService: TagService,
        private val attributeService: AttributeService
) : VerticalLayout() {

    private val ui = UI.getCurrent()

    private var filter = DocImportService.Filter()

    private val progressBar = ProgressBar().apply { setWidthFull() }

    private val progressText = Text("")

    private val itemContainer = Div().apply {
        setSizeFull()
        element.style["display"] = "flex"
        element.style["flexWrap"] = "wrap"
    }

    private val tagFilter = ComboBox("", tagService.findAll()).apply {
        placeholder = t("tag")
        isClearButtonVisible = true
        isPreventInvalidInput = true
        isAllowCustomValue = false
        setItemLabelGenerator { it.name }
        addValueChangeListener { refreshFilter() }
    }

    private val attributeFilter = ComboBox("", attributeService.findAll()).apply {
        placeholder = t("attribute")
        isClearButtonVisible = true
        isPreventInvalidInput = true
        isAllowCustomValue = false
        setItemLabelGenerator { it.name }
        addValueChangeListener { refreshFilter() }
    }

    private val textFilter = TextField("", "Text").apply {
        isClearButtonVisible = true
        addValueChangeListener { refreshFilter() }
        valueChangeMode = ValueChangeMode.LAZY
        width = "25vw"
    }

    init {
        val shrinkButton = Button(VaadinIcon.MINUS_CIRCLE.create()) { shrink() }
        val growButton = Button(VaadinIcon.PLUS_CIRCLE.create()) { grow() }

        val progress = VerticalLayout(progressText, progressBar).apply { setWidthFull() }
        val header = HorizontalLayout(tagFilter, attributeFilter, textFilter, shrinkButton, growButton).apply { setWidthFull() }
        add(progress, header, itemContainer)
        fill()
    }

    private fun grow() {
        val options = Options.get()
        if (options.view.docCardSize < 400) {
            options.view.docCardSize += 10
            options.save()
            itemContainer.children.filter { it is DocCard }.forEach { (it as DocCard).resize() }
        }
    }

    private fun shrink() {
        val options = Options.get()
        if (options.view.docCardSize > 100) {
            options.view.docCardSize -= 10
            options.save()
            itemContainer.children.filter { it is DocCard }.forEach { (it as DocCard).resize() }
        }
    }

    private fun fill() {
        itemContainer.removeAll()
        val items = docImportService.findByFilter(filter).map { builderFactory.docs().card(it) }
        ui.access { items.forEach { itemContainer.add(it) } }
    }

    private fun refreshFilter() {
        filter = DocImportService.Filter(
                tag = tagFilter.optionalValue.orElse(null),
                attribute = attributeFilter.optionalValue.orElse(null),
                text = textFilter.optionalValue.orElse(null)
        )
        fill()
    }
}