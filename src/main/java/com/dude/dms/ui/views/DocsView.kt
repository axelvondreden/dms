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
import com.dude.dms.extensions.*
import com.dude.dms.ui.Const
import com.dude.dms.ui.components.cards.DocCard
import com.dude.dms.ui.components.misc.ViewPageSelector
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.radiobutton.RadioGroupVariant
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.vaadin.gatanaso.MultiselectComboBox
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

    private var itemContainer: Div

    private lateinit var tagIncludeFilter: MultiselectComboBox<Tag>
    private lateinit var tagIncludeVariant: RadioButtonGroup<String>
    private lateinit var tagExcludeFilter: MultiselectComboBox<Tag>

    private lateinit var attributeIncludeFilter: MultiselectComboBox<Attribute>
    private lateinit var attributeIncludeVariant: RadioButtonGroup<String>
    private lateinit var attributeExcludeFilter: MultiselectComboBox<Attribute>

    private lateinit var textFilter: TextField
    private lateinit var fromFilter: DatePicker
    private lateinit var toFilter: DatePicker

    private lateinit var sortFilter: ComboBox<Pair<String, Sort>>

    private lateinit var pageSelector: ViewPageSelector

    init {
        eventManager.register(this, Doc::class, EventType.CREATE) { softReload(viewUI) }
        eventManager.register(this, Doc::class, EventType.UPDATE) { updateDoc(it, viewUI) }
        eventManager.register(this, Doc::class, EventType.DELETE) { deleteDoc(it, viewUI) }
        eventManager.register(this, Tag::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { softReload(viewUI) }
        eventManager.register(this, Attribute::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { refreshFilterOptions() }

        verticalLayout(isPadding = false, isSpacing = false) {
            horizontalLayout {
                setWidthFull()

                textFilter = textField {
                    setWidthFull()
                    placeholder = "Text"
                    isClearButtonVisible = true
                    addValueChangeListener { fill(viewUI) }
                    valueChangeMode = ValueChangeMode.LAZY
                }
                fromFilter = datePicker {
                    placeholder = t("from")
                    addValueChangeListener { fill(viewUI) }
                    isClearButtonVisible = true
                }
                toFilter = datePicker {
                    placeholder = t("to")
                    addValueChangeListener { fill(viewUI) }
                    isClearButtonVisible = true
                }
                sortFilter = comboBox {
                    setItems(sorts)
                    isPreventInvalidInput = true
                    isAllowCustomValue = false
                    value = sorts[0]
                    setItemLabelGenerator { it.first }
                    addValueChangeListener { fill(viewUI) }
                }
                iconButton(VaadinIcon.MINUS_CIRCLE.create()) {
                    tooltip(t("zoom.out"))
                    onLeftClick { shrink() }
                }
                iconButton(VaadinIcon.PLUS_CIRCLE.create()) {
                    tooltip(t("zoom.in"))
                    onLeftClick { grow() }
                }
                pageSelector = viewPageSelector()
            }
            details(t("search.advanced")) {
                element.style["width"] = "100%"

                content {
                    verticalLayout(isPadding = false, isSpacing = false) {
                        setWidthFull()

                        horizontalLayout {
                            setWidthFull()
                            alignItems = FlexComponent.Alignment.CENTER

                            tagIncludeVariant = radioButtonGroup {
                                setItems(t("all"), t("any"))
                                addThemeVariants(RadioGroupVariant.LUMO_VERTICAL)
                                value = t("all")
                                addValueChangeListener { fill(viewUI) }
                            }
                            tagIncludeFilter = multiSelectComboBox("", tagService.findAll()) {
                                width = "20vw"
                                maxWidth = "20vw"
                                placeholder = t("tags")
                                isClearButtonVisible = true
                                isAllowCustomValues = false
                                setItemLabelGenerator { it.name }
                                addValueChangeListener { fill(viewUI) }
                            }
                            tagExcludeFilter = multiSelectComboBox("", tagService.findAll()) {
                                width = "20vw"
                                maxWidth = "20vw"
                                placeholder = t("tags.exclude")
                                isClearButtonVisible = true
                                isAllowCustomValues = false
                                setItemLabelGenerator { it.name }
                                addValueChangeListener { fill(viewUI) }
                            }
                        }
                        horizontalLayout {
                            setWidthFull()
                            alignItems = FlexComponent.Alignment.CENTER

                            attributeIncludeVariant = radioButtonGroup {
                                setItems(t("all"), t("any"))
                                addThemeVariants(RadioGroupVariant.LUMO_VERTICAL)
                                value = t("all")
                                addValueChangeListener { fill(viewUI) }
                            }
                            attributeIncludeFilter = multiSelectComboBox("", attributeService.findAll()) {
                                width = "20vw"
                                maxWidth = "20vw"
                                placeholder = t("attributes")
                                isClearButtonVisible = true
                                isAllowCustomValues = false
                                setItemLabelGenerator { it.name }
                                addValueChangeListener { fill(viewUI) }
                            }
                            attributeExcludeFilter = multiSelectComboBox("", attributeService.findAll()) {
                                width = "20vw"
                                maxWidth = "20vw"
                                placeholder = t("attributes.exclude")
                                isClearButtonVisible = true
                                isAllowCustomValues = false
                                setItemLabelGenerator { it.name }
                                addValueChangeListener { fill(viewUI) }
                            }
                        }
                    }
                }
            }
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
        tagIncludeFilter.setItems(tagService.findAll())
        attributeIncludeFilter.setItems(attributeService.findAll())
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
        val docs = docService.findByFilter(getFilter(), PageRequest.of(pageSelector.page, pageSelector.pageSize.value, sortFilter.value.second))
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

    private fun getFilter() = DocService.Filter(
            includeAllTags = t("all") == tagIncludeVariant.value,
            includeAllAttributes = t("all") == attributeIncludeVariant.value,
            includedTags = tagIncludeFilter.optionalValue.orElse(null),
            excludedTags = tagExcludeFilter.optionalValue.orElse(null),
            includedAttributes = attributeIncludeFilter.optionalValue.orElse(null),
            excludedAttributes = attributeExcludeFilter.optionalValue.orElse(null),
            from = fromFilter.optionalValue.orElse(null),
            to = toFilter.optionalValue.orElse(null),
            text = textFilter.optionalValue.orElse(null)
    )

    override fun setParameter(beforeEvent: BeforeEvent, @OptionalParameter t: String?) {
        if (!t.isNullOrEmpty()) {
            val parts = t.split(":").toTypedArray()
            if ("tag".equals(parts[0], ignoreCase = true)) {
                tagIncludeFilter.value = setOf(tagService.findByName(parts[1]))
            }
        }
    }
}