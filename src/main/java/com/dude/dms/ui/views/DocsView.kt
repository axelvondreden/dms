package com.dude.dms.ui.views

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.extensions.docCard
import com.dude.dms.extensions.viewPageSelector
import com.dude.dms.ui.Const
import com.dude.dms.ui.components.cards.DocCard
import com.dude.dms.ui.components.misc.ViewPageSelector
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
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
        private val attributeService: AttributeService,
        private val fileManager: FileManager,
        eventManager: EventManager
) : VerticalLayout(), HasUrlParameter<String?> {

    private var scheduler = Timer()

    private val sorts = listOf(
            "${t("date")} ${t("descending")}" to Sort.by(Sort.Direction.DESC, "documentDate"),
            "${t("date")} ${t("ascending")}" to Sort.by(Sort.Direction.ASC, "documentDate"),
            "${t("created")} ${t("descending")}" to Sort.by(Sort.Direction.DESC, "insertTime"),
            "${t("created")} ${t("ascending")}" to Sort.by(Sort.Direction.ASC, "insertTime")
    )

    private val viewUI = UI.getCurrent()

    private var filter = DocService.Filter()

    private var itemContainer: Div

    private lateinit var tagFilter: ComboBox<Tag>

    private lateinit var attributeFilter: ComboBox<Attribute>

    private lateinit var textFilter: TextField

    private lateinit var sortFilter: ComboBox<Pair<String, Sort>>

    private lateinit var pageSelector: ViewPageSelector

    init {
        eventManager.register(this, Doc::class, EventType.CREATE) { softReload(viewUI) }
        eventManager.register(this, Doc::class, EventType.UPDATE) { updateDoc(it, viewUI) }
        eventManager.register(this, Doc::class, EventType.DELETE) { deleteDoc(it, viewUI) }
        eventManager.register(this, Tag::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { softReload(viewUI) }
        eventManager.register(this, Attribute::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { refreshFilterOptions() }

        horizontalLayout {
            setWidthFull()

            tagFilter = comboBox {
                setItems(tagService.findAll())
                placeholder = t("tag")
                isClearButtonVisible = true
                isPreventInvalidInput = true
                isAllowCustomValue = false
                setItemLabelGenerator { it.name }
                addValueChangeListener { refreshFilter() }
            }
            attributeFilter = comboBox {
                setItems(attributeService.findAll())
                placeholder = t("attribute")
                isClearButtonVisible = true
                isPreventInvalidInput = true
                isAllowCustomValue = false
                setItemLabelGenerator { it.name }
                addValueChangeListener { refreshFilter() }
            }
            textFilter = textField {
                width = "25vw"
                placeholder = "Text"
                isClearButtonVisible = true
                addValueChangeListener { refreshFilter() }
                valueChangeMode = ValueChangeMode.LAZY
            }
            sortFilter = comboBox {
                setItems(sorts)
                isPreventInvalidInput = true
                isAllowCustomValue = false
                value = sorts[0]
                setItemLabelGenerator { it.first }
                addValueChangeListener { refreshFilter() }
            }
            iconButton(VaadinIcon.MINUS_CIRCLE.create()) {
                onLeftClick { shrink() }
            }
            iconButton(VaadinIcon.PLUS_CIRCLE.create()) {
                onLeftClick { grow() }
            }
            pageSelector = viewPageSelector()
        }
        itemContainer = div {
            setSizeFull()
            style["display"] = "flex"
            style["flexWrap"] = "wrap"
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
        refreshFilterOptions()
    }

    private fun refreshFilterOptions() {
        tagFilter.setItems(tagService.findAll())
        attributeFilter.setItems(attributeService.findAll())
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
        scheduler.schedule(1000) {
            fill(ui)
        }
    }

    private fun fill(ui: UI) {
        ui.access {
            itemContainer.removeAll()
            pageSelector.items = docService.countByFilter(filter).toInt()
        }
        docService.findByFilter(filter, PageRequest.of(pageSelector.page, pageSelector.pageSize.value, sortFilter.value.second)).forEach { doc ->
            val dc = DocContainer(doc).apply { thumbnail = fileManager.getImage(guid) }
            ui.access {
                itemContainer.docCard(dc)
            }
        }
    }

    private fun refreshFilter() {
        filter = DocService.Filter(
                tag = tagFilter.optionalValue.orElse(null),
                attribute = attributeFilter.optionalValue.orElse(null),
                text = textFilter.optionalValue.orElse(null)
        )
        fill(viewUI)
    }

    override fun setParameter(beforeEvent: BeforeEvent, @OptionalParameter t: String?) {
        if (!t.isNullOrEmpty()) {
            val parts = t.split(":").toTypedArray()
            if ("tag".equals(parts[0], ignoreCase = true)) {
                tagFilter.value = tagService.findByName(parts[1])
            }
        }
    }
}