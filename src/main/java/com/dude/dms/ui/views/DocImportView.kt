package com.dude.dms.ui.views

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.*
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.polling.DocImportService
import com.dude.dms.brain.t
import com.dude.dms.ui.Const
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.cards.DocImportCard
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
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
import java.time.LocalDateTime


@Route(value = Const.PAGE_DOCIMPORT, layout = MainView::class)
@PageTitle("Doc Import")
class DocImportView(
        private val builderFactory: BuilderFactory,
        private val docImportService: DocImportService,
        private val docService: DocService,
        private val lineService: LineService,
        private val wordService: WordService,
        tagService: TagService,
        attributeService: AttributeService
) : VerticalLayout() {

    private var filter = DocImportService.Filter()

    private val progressBar = ProgressBar().apply { setWidthFull() }

    private val progressText = Text("")

    private val docs = mutableSetOf<DocContainer>()

    private val selected = mutableSetOf<DocContainer>()

    private var loading = false

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
        val importButton = Button("Import", VaadinIcon.PLUS_CIRCLE.create()) { createDocs() }.apply { addThemeVariants(ButtonVariant.LUMO_PRIMARY) }
        val refreshButton = Button(t("refresh"), VaadinIcon.REFRESH.create()) { refresh() }
        val selectAllButton = Button(t("select.all"), VaadinIcon.SELECT.create()) { selectAll() }

        val progress = VerticalLayout(progressText, progressBar).apply { setWidthFull() }
        val header = HorizontalLayout(tagFilter, attributeFilter, textFilter, shrinkButton, growButton, importButton, refreshButton, selectAllButton).apply { setWidthFull() }
        add(progress, header, itemContainer)
        fill()
    }

    private fun fill(ui: UI = UI.getCurrent(), update: Boolean = false) {
        if (!update) {
            itemContainer.removeAll()
            docs.clear()
            selected.clear()
        }
        val newDocs = docImportService.findByFilter(filter).filter { if (update) it !in docs else true }
        docs.addAll(newDocs)
        ui.access { newDocs.forEach { itemContainer.add(builderFactory.docs().importCard(it)) } }
    }

    fun refresh() {
        if (loading) return
        loading = true
        progressBar.value = 0.0
        if (docImportService.progress < 1.0) {
            Thread { docImportService.import() }.start()
        }
        val ui = UI.getCurrent()
        Thread {
            var process = docImportService.progress
            while (process < 1.0) {
                ui.access { progressBar.value = process }
                process = docImportService.progress
                Thread.sleep(100)
            }
            Thread.sleep(50)
            ui.access { progressBar.value = 1.0 }
            loading = false
        }.start()

        Thread {
            var process = docImportService.progress
            while (process < 1.0) {
                fill(ui, true)
                process = docImportService.progress
                Thread.sleep(1000)
            }
            Thread.sleep(50)
            ui.access { progressBar.value = 1.0 }
            loading = false
        }.start()
    }

    private fun createDocs() {
        selected.forEach { docContainer ->
            val lines = docContainer.lineEntities
            val doc = Doc(
                    docContainer.guid,
                    docContainer.date,
                    LocalDateTime.now(),
                    if (lines.isNotEmpty()) docService.getFullTextMemory(lines) else null,
                    docContainer.tags
            )
            docService.create(doc)
            lines.forEach { line ->
                line.doc = doc
                lineService.create(line)
                line.words.forEach { word ->
                    wordService.create(word)
                }
            }
            docContainer.file?.delete()
        }
        fill()
    }

    private fun grow() {
        val options = Options.get()
        if (options.view.docCardSize < 400) {
            options.view.docCardSize += 10
            options.save()
            itemContainer.children.filter { it is DocImportCard }.forEach { (it as DocImportCard).resize() }
        }
    }

    private fun shrink() {
        val options = Options.get()
        if (options.view.docCardSize > 100) {
            options.view.docCardSize -= 10
            options.save()
            itemContainer.children.filter { it is DocImportCard }.forEach { (it as DocImportCard).resize() }
        }
    }

    private fun selectAll() {
        selected.clear()
        selected.addAll(docs)
        itemContainer.children.filter { it is DocImportCard }.forEach {  }
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