package com.dude.dms.ui.views

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.extensions.docCard
import com.dude.dms.extensions.searchBar
import com.dude.dms.extensions.viewPageSelector
import com.dude.dms.ui.Const
import com.dude.dms.ui.components.cards.DocCard
import com.dude.dms.ui.components.misc.SearchBar
import com.dude.dms.ui.components.misc.ViewPageSelector
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.*
import kotlin.concurrent.schedule
import kotlin.streams.toList


@Route(value = Const.PAGE_DOCS, layout = MainView::class)
@RouteAlias(value = Const.PAGE_ROOT, layout = MainView::class)
@PageTitle("Docs")
class DocsView(
        private val docService: DocService,
        private val tagService: TagService,
        private val fileManager: FileManager,
        eventManager: EventManager
) : VerticalLayout(), HasUrlParameter<String?> {

    private var scheduler = Timer()

    private val viewUI = UI.getCurrent()

    private var itemContainer: Div

    private lateinit var pageSelector: ViewPageSelector

    private lateinit var searchBar: SearchBar

    private lateinit var sortFilter: ComboBox<Pair<String, Sort>>

    init {
        eventManager.register(this, Doc::class, EventType.CREATE) { softReload(viewUI) }
        eventManager.register(this, Doc::class, EventType.UPDATE) { updateDoc(it, viewUI) }
        eventManager.register(this, Doc::class, EventType.DELETE) { deleteDoc(it, viewUI) }
        eventManager.register(this, Tag::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { softReload(viewUI) }
        eventManager.register(this, Attribute::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { softReload(viewUI) }

        horizontalLayout {
            searchBar = searchBar {
                onChange = { fill(viewUI) }
            }
        }
        itemContainer = div {
            setSizeFull()
            style["display"] = "flex"
            style["flexWrap"] = "wrap"
        }
        horizontalLayout {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            style["position"] = "absolute"
            style["bottom"] = "0"
            style["paddingBottom"] = "4px"
            style["paddingTop"] = "4px"
            style["borderTop"] = "1px solid var(--lumo-contrast-10pct)"

            label("${t("sort")}:")
            sortFilter = comboBox {
                setItems(sorts)
                isPreventInvalidInput = true
                isAllowCustomValue = false
                value = sorts[0]
                setItemLabelGenerator { it.first }
                addValueChangeListener { scheduleFill(viewUI) }
            }
            div { width = "2em" }
            label("${t("zoom")}:")
            iconButton(VaadinIcon.MINUS_CIRCLE.create()) {
                onLeftClick { shrink() }
            }
            iconButton(VaadinIcon.PLUS_CIRCLE.create()) {
                onLeftClick { grow() }
            }
            div { width = "2em" }
            pageSelector = viewPageSelector()
        }

        pageSelector.setChangeListener { scheduleFill(viewUI) }
        scheduleFill(viewUI)
    }

    private fun updateDoc(doc: Doc, ui: UI) {
        ui.access {
            itemContainer.children.toList().filterIsInstance<DocCard>().firstOrNull { it.docContainer.doc?.guid == doc.guid }?.fill()
        }
    }

    private fun deleteDoc(doc: Doc, ui: UI) {
        ui.access {
            itemContainer.children.toList().filterIsInstance<DocCard>().firstOrNull { it.docContainer.doc?.guid == doc.guid }?.let {
                itemContainer.remove(it)
            }
        }
    }

    private fun softReload(ui: UI) {
        scheduleFill(ui)
        searchBar.refresh()
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

    private fun scheduleFill(ui: UI) {
        scheduler.cancel()
        scheduler = Timer()
        scheduler.schedule(500) {
            fill(ui)
        }
    }

    private fun fill(ui: UI) {
        val docs = docService.findByFilter(searchBar.filter, PageRequest.of(pageSelector.page, pageSelector.pageSize.value, sortFilter.value.second))
        ui.access {
            itemContainer.removeAll()
            pageSelector.items = docs.size
        }
        docs.forEach { doc ->
            val dc = DocContainer(doc).apply { thumbnail = fileManager.getImage(guid) }
            ui.access {
                itemContainer.docCard(dc)
            }
        }
    }

    override fun setParameter(beforeEvent: BeforeEvent, @OptionalParameter t: String?) {
        if (!t.isNullOrEmpty()) {
            val parts = t.split(":").toTypedArray()
            if ("tag".equals(parts[0], ignoreCase = true)) {
                searchBar.tagIncludeFilter.value = setOf(tagService.findByName(parts[1]))
            }
        }
    }

    companion object {
        private val sorts = listOf(
                "${t("date")} ${t("descending")}" to Sort.by(Sort.Direction.DESC, "documentDate"),
                "${t("date")} ${t("ascending")}" to Sort.by(Sort.Direction.ASC, "documentDate"),
                "${t("created")} ${t("descending")}" to Sort.by(Sort.Direction.DESC, "insertTime"),
                "${t("created")} ${t("ascending")}" to Sort.by(Sort.Direction.ASC, "insertTime")
        )
    }
}