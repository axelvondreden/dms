package com.dude.dms.ui.views

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.Const
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.cards.DocCard
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.*
import org.springframework.data.domain.Sort
import java.util.*
import kotlin.concurrent.schedule


@Route(value = Const.PAGE_DOCS, layout = MainView::class)
@RouteAlias(value = Const.PAGE_ROOT, layout = MainView::class)
@PageTitle("Docs")
class DocsView(
        private val builderFactory: BuilderFactory,
        private val docService: DocService,
        private val tagService: TagService,
        private val attributeService: AttributeService,
        eventManager: EventManager
) : VerticalLayout(), HasUrlParameter<String?> {

    private var scheduler = Timer()

    private val sorts = listOf(
            "${t("date")} ${t("descending")}" to Sort.by(Sort.Direction.DESC, "documentDate"),
            "${t("date")} ${t("ascending")}" to Sort.by(Sort.Direction.ASC, "documentDate"),
            "${t("created")} ${t("descending")}" to Sort.by(Sort.Direction.DESC, "insertTime"),
            "${t("created")} ${t("ascending")}" to Sort.by(Sort.Direction.ASC, "insertTime")
    )

    private val ui = UI.getCurrent()

    private var filter = DocService.Filter()

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

    private val sortFilter = ComboBox("", sorts).apply {
        isPreventInvalidInput = true
        isAllowCustomValue = false
        value = sorts[0]
        setItemLabelGenerator { it.first }
        addValueChangeListener { refreshFilter() }
    }

    init {
        eventManager.register(this, Doc::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { softReload(ui) }
        eventManager.register(this, Tag::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { softReload(ui) }
        eventManager.register(this, Attribute::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { refreshFilterOptions() }

        val shrinkButton = Button(VaadinIcon.MINUS_CIRCLE.create()) { shrink() }
        val growButton = Button(VaadinIcon.PLUS_CIRCLE.create()) { grow() }

        val header = HorizontalLayout(tagFilter, attributeFilter, textFilter, sortFilter, shrinkButton, growButton).apply { setWidthFull() }
        add(header, itemContainer)
        scheduleFill(ui)
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
            ui.access { fill() }
        }
    }

    private fun fill() {
        itemContainer.removeAll()
        val items = docService.findByFilter(filter, sortFilter.value.second).map { builderFactory.docs().card(DocContainer(it)) }
        ui.access { items.forEach { itemContainer.add(it) } }
    }

    private fun refreshFilter() {
        filter = DocService.Filter(
                tag = tagFilter.optionalValue.orElse(null),
                attribute = attributeFilter.optionalValue.orElse(null),
                text = textFilter.optionalValue.orElse(null)
        )
        fill()
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