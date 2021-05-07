package com.dude.dms.ui.views

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.DocService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.Const
import com.dude.dms.ui.components.cards.DocCard
import com.dude.dms.ui.components.misc.SearchBar
import com.dude.dms.ui.components.misc.ViewPageSelector
import com.dude.dms.utils.docCard
import com.dude.dms.utils.searchBar
import com.dude.dms.utils.viewPageSelector
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.*
import org.springframework.data.domain.PageRequest
import java.util.*
import kotlin.concurrent.schedule
import kotlin.streams.toList


@Route(value = Const.PAGE_DOCS, layout = MainView::class)
@RouteAlias(value = Const.PAGE_ROOT, layout = MainView::class)
@PageTitle("Docs")
class DocsView(
        private val docService: DocService,
        eventManager: EventManager
) : VerticalLayout(), HasUrlParameter<String?> {

    private var scheduler = Timer()

    private val viewUI = UI.getCurrent()

    private val itemContainer: Div

    private val searchBar: SearchBar

    private lateinit var pageSelector: ViewPageSelector

    private lateinit var itemCount: Text

    private var filter = ""

    init {
        eventManager.register(this, Doc::class, EventType.CREATE) { softReload(viewUI) }
        eventManager.register(this, Doc::class, EventType.UPDATE) { updateDoc(it, viewUI) }
        eventManager.register(this, Doc::class, EventType.DELETE) { deleteDoc(it, viewUI) }
        eventManager.register(this, Tag::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { softReload(viewUI) }
        eventManager.register(this, Attribute::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { softReload(viewUI) }

        style["paddingTop"] = "0px"
        style["paddingBottom"] = "0px"
        setHeightFull()

        searchBar = searchBar {
            style["position"] = "sticky"
            style["top"] = "0"
            style["zIndex"] = "1"
            style["borderBottom"] = "1px solid var(--lumo-contrast-10pct)"
            style["backgroundColor"] = "var(--lumo-base-color)"
            onChange = {
                filter = it
                fill(viewUI)
            }
        }
        itemContainer = div {
            setWidthFull()
            style["display"] = "flex"
            style["flexWrap"] = "wrap"
            style["paddingBottom"] = "40px"
        }
        horizontalLayout {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            alignItems = FlexComponent.Alignment.CENTER
            style["position"] = "sticky"
            style["bottom"] = "0"
            style["marginTop"] = "auto"
            style["borderTop"] = "1px solid var(--lumo-contrast-10pct)"
            style["backgroundColor"] = "var(--lumo-base-color)"

            label("${t("zoom")}:")
            iconButton(VaadinIcon.MINUS_CIRCLE.create()) {
                onLeftClick { shrink() }
            }
            iconButton(VaadinIcon.PLUS_CIRCLE.create()) {
                onLeftClick { grow() }
            }
            div { width = "2em" }
            pageSelector = viewPageSelector()
            div { width = "2em" }
            itemCount = text(t("items") + ":")
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
        val docs = docService.findByFilter(filter, PageRequest.of(pageSelector.page, pageSelector.pageSize.value))
        val count = docService.countByFilter(filter)
        ui.access {
            itemContainer.removeAll()
            pageSelector.items = count
            itemCount.text = "${t("items")}: ${docs.size}"
        }
        docs.forEach { doc ->
            val dc = DocContainer(doc)
            ui.access {
                itemContainer.docCard(dc)
            }
        }
    }

    override fun setParameter(beforeEvent: BeforeEvent, @OptionalParameter t: String?) {
        if (!t.isNullOrEmpty()) {
            val parts = t.split(":").toTypedArray()
            if ("tag".equals(parts[0], ignoreCase = true)) {
                searchBar.textFilter.value = "${t("tag")} = ${parts[1]}"
            }
        }
    }
}
