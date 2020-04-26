package com.dude.dms.ui.views

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.brain.polling.DocImportService
import com.dude.dms.brain.t
import com.dude.dms.ui.Const
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.cards.DocImportCard
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.progressbar.ProgressBar
import com.vaadin.flow.component.splitlayout.SplitLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import dev.mett.vaadin.tooltip.Tooltips
import kotlin.streams.toList


@Route(value = Const.PAGE_DOCIMPORT, layout = MainView::class)
@PageTitle("Doc Import")
class DocImportView(builderFactory: BuilderFactory, private val docImportService: DocImportService, private val docParser: DocParser) : VerticalLayout() {

    private val progressBar = ProgressBar().apply { setWidthFull() }

    private val progressText = Text("").apply { setWidthFull() }

    private val docs = mutableSetOf<DocContainer>()

    private var loading = false

    private val itemContainer = HorizontalLayout().apply {
        setSizeFull()
        style["overflowY"] = "hidden"
    }

    private val importButton = Button("Import", VaadinIcon.PLUS_CIRCLE.create()) { createDocs() }.apply {
        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        width = "250px"
    }

    private val itemPreview = builderFactory.docs().importPreview().apply {
        onDone = { docContainer ->
            docContainer.done = true
            var index = -1
            val cards = itemContainer.children.filter { it is DocImportCard }.map { it as DocImportCard }.toList()
            cards.firstOrNull { it.docContainer == docContainer }?.let {
                it.style["backgroundColor"] = "rgba(0, 255, 0, 0.3)"
                index = cards.indexOf(it) + 1
            }
            importButton.text = "Import ${docs.count { it.done }} / ${docs.count()}"
            while (index > 0 && index < cards.size) {
                if (!cards[index].docContainer.done) {
                    select(cards[index].docContainer)
                    break
                }
                index++
            }
        }
    }

    init {
        setSizeFull()
        isSpacing = false

        val refreshButton = Button(t("refresh"), VaadinIcon.REFRESH.create()) { refresh() }.apply { width = "250px" }
        val rerunRules = Button(t("rules.rerun"), VaadinIcon.MAGIC.create()) { rerunRules() }.apply { width = "250px" }
        Tooltips.getCurrent().setTooltip(rerunRules, t("rules.rerun.tooltip"))

        val progress = VerticalLayout(progressBar, progressText).apply {
            setWidthFull()
            isSpacing = false
            isPadding = false
        }
        val header = HorizontalLayout(refreshButton, rerunRules, progress, importButton).apply { setWidthFull() }
        val split = SplitLayout(itemContainer, itemPreview).apply {
            setSizeFull()
            orientation = SplitLayout.Orientation.VERTICAL
            setSplitterPosition(15.0)
            setPrimaryStyle("minHeight", "100px")
            setPrimaryStyle("maxHeight", "300px")
        }
        add(header, split)
        refresh()
    }

    private fun fill(ui: UI = UI.getCurrent(), update: Boolean = false) {
        if (!update) {
            itemContainer.removeAll()
            docs.clear()
        }
        val newDocs = docImportService.findAll().filter { if (update) it !in docs else true }
        docs.addAll(newDocs)
        ui.access {
            importButton.text = "Import ${docs.count { it.done }} / ${docs.count()}"
            newDocs.forEach { dc ->
                val dic = DocImportCard(dc).apply {
                    addClickListener { select(dc) }
                    if (dc.done) style["backgroundColor"] = "rgba(0, 255, 0, 0.3)"
                }
                ContextMenu().apply {
                    target = dic
                    addItem(t("delete")) { delete(dc) }
                }
                itemContainer.add(dic)
            }
        }
    }

    private fun rerunRules() {
        docs.filter { !it.done }.forEach { it.tags = docParser.discoverTags(it.pages) }
        itemPreview.clear()
        fill()
    }

    private fun select(dc: DocContainer) {
        itemPreview.fill(dc)
        itemContainer.children.filter { it is DocImportCard }.forEach { (it as DocImportCard).select(it.docContainer == dc) }
    }

    fun refresh() {
        if (loading) return
        loading = true
        fill()
        progressBar.value = 0.0
        progressText.text = ""
        if (docImportService.progress < 1.0) {
            Thread { docImportService.import() }.start()
        }
        val ui = UI.getCurrent()
        Thread {
            var process = docImportService.progress
            while (process < 1.0) {
                ui.access {
                    progressBar.value = process
                    progressText.text = docImportService.progressText
                }
                process = docImportService.progress
                Thread.sleep(100)
            }
            Thread.sleep(50)
            ui.access {
                progressBar.value = 1.0
                progressText.text = t("done")
            }
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
            ui.access {
                progressBar.value = 1.0
                progressText.text = t("done")
            }
            loading = false
        }.start()
    }

    private fun createDocs() {
        val done = docs.filter { it.done }
        done.forEach { docImportService.create(it) }
        docs.removeAll(done)
        itemPreview.clear()
        fill()
    }

    private fun delete(docContainer: DocContainer) {
        docs.remove(docContainer)
        docImportService.delete(docContainer)
        itemPreview.clear()
        fill()
    }
}