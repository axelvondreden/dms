package com.dude.dms.ui.components.search

import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.github.appreciated.app.layout.component.appbar.IconButton
import com.github.appreciated.card.Card
import com.github.appreciated.card.RippleClickableCard
import com.github.appreciated.card.label.SecondaryLabel
import com.github.appreciated.card.label.TitleLabel
import com.github.appreciated.ironoverlay.IronOverlay
import com.github.appreciated.ironoverlay.VerticalOrientation
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.data.value.ValueChangeMode
import org.vaadin.gatanaso.MultiselectComboBox

class DmsSearchOverlayView(private val builderFactory: BuilderFactory) : IronOverlay() {

    private val searchField = TextField().apply {
        style["--lumo-contrast-10pct"] = "transparent"
        addValueChangeListener { event -> showResults(event.value) }
        valueChangeMode = ValueChangeMode.EAGER
        setWidthFull()
    }

    private val closeButton = IconButton(VaadinIcon.ARROW_LEFT.create()).apply {
        addClickListener {
            searchField.clear()
            close()
        }
    }

    private val resultsWrapper = VerticalLayout().apply {
        setSizeFull()
        isMargin = false
        style["overflow"] = "auto"
        style["overflow-y"] = "auto"
        style["max-width"] = "100%"
        style["min-width"] = "40%"
        style["--lumo-size-m"] = "var(--lumo-size-xl)"
        style["--lumo-contrast-10pct"] = "transparent"
        setHeightFull()
        width = "unset"
    }

    private val entityMultiselect = MultiselectComboBox<String>().apply {
        setItems(t("docs"), t("tags"))
        select(t("docs"), t("tags"))
        width = "30%"
        addValueChangeListener { showResults(searchField.value) }
    }

    private val caseSensitiveCheckbox = Checkbox("Case sensitive").apply { addValueChangeListener { showResults(searchField.value) } }

    private var docDataProvider: DataProvider<DocSearchResult, String>? = null
    private var tagDataProvider: DataProvider<TagSearchResult, String>? = null
    private var docService: DocService? = null
    private var tagService: TagService? = null

    init {
        element.style["width"] = "100%"
        verticalAlign = VerticalOrientation.TOP

        val searchFieldWrapper = HorizontalLayout(closeButton, searchField).apply {
            setWidthFull()
            alignItems = FlexComponent.Alignment.CENTER
            style["background"] = "var(--app-layout-bar-background-base-color)"
            style["height"] = "var(--app-bar-height)"
            style["box-shadow"] = "var(--app-layout-bar-shadow)"
            style["padding"] = "var(--app-layout-bar-padding)"
            style["flex-shrink"] = "0"
            style["z-index"] = "1"
        }
        val configWrapper = HorizontalLayout(Label(t("search")), entityMultiselect, caseSensitiveCheckbox).apply {
            setWidthFull()
            alignItems = FlexComponent.Alignment.CENTER
            style["background"] = "var(--app-layout-bar-background-base-color)"
            style["box-shadow"] = "var(--app-layout-bar-shadow)"
            style["padding"] = "var(--app-layout-bar-padding)"
            style["flex-shrink"] = "0"
            style["z-index"] = "1"
        }
        val wrapper = VerticalLayout(searchFieldWrapper, configWrapper, resultsWrapper).apply {
            setSizeFull()
            alignItems = FlexComponent.Alignment.CENTER
            isMargin = false
            isPadding = false
            isSpacing = false
            style["max-width"] = "100vw"
            style["height"] = "100vh"
        }
        add(wrapper)
    }

    private fun showResults(value: String?) {
        resultsWrapper.removeAll()
        if (value == null || value.isEmpty()) return
        if (t("docs") in entityMultiselect.selectedItems) {
            val title = Card(TitleLabel("Docs " + docDataProvider!!.size(Query(value))))
            title.setWidthFull()
            title.setBackground("var(--lumo-base-color)")
            resultsWrapper.add(title)
            docDataProvider!!.fetch(Query(value)).forEach { result ->
                val card = RippleClickableCard(ComponentEventListener { result.onClick() }, SecondaryLabel(result.header), result.body).apply {
                    setWidthFull()
                    setBackground("var(--lumo-base-color)")
                }
                resultsWrapper.add(card)
            }
        }
        if (t("tags") in entityMultiselect.selectedItems) {
            val title = Card(TitleLabel("Tags " + tagDataProvider!!.size(Query(value)))).apply {
                setWidthFull()
                setBackground("var(--lumo-base-color)")
            }
            resultsWrapper.add(title)
            tagDataProvider!!.fetch(Query(value)).forEach { result ->
                val card = RippleClickableCard(ComponentEventListener { result.onClick() }, SecondaryLabel(result.header), result.body).apply {
                    setWidthFull()
                    setBackground("var(--lumo-base-color)")
                }
                resultsWrapper.add(card)
            }
        }
    }

    fun initDataproviders(docService: DocService, tagService: TagService) {
        this.docService = docService
        this.tagService = tagService
        docDataProvider = DataProvider.fromFilteringCallbacks({ query -> if (query.filter.isPresent) searchDocs(query.filter.get()).stream() else null })
        { query -> if (query.filter.isPresent) countDocs(query.filter.get()) else 0 }

        tagDataProvider = DataProvider.fromFilteringCallbacks({ query -> if (query.filter.isPresent) searchTags(query.filter.get()).stream() else null })
        { query -> if (query.filter.isPresent) countTags(query.filter.get()) else 0 }
    }

    private fun searchDocs(filter: String) = when {
        caseSensitiveCheckbox.value -> docService!!.findTop10ByRawTextContaining(filter).map { builderFactory.docs().searchResult(it, filter).build() }
        else -> docService!!.findTop10ByRawTextContainingIgnoreCase(filter).map { builderFactory.docs().searchResult(it, filter).build() }
    }

    private fun countDocs(filter: String) = when {
        caseSensitiveCheckbox.value -> docService!!.countByRawTextContaining(filter).toInt()
        else -> docService!!.countByRawTextContainingIgnoreCase(filter).toInt()
    }

    private fun searchTags(filter: String) = when {
        caseSensitiveCheckbox.value -> tagService!!.findTop10ByNameContaining(filter).map { builderFactory.tags().searchResult(it).build() }
        else -> tagService!!.findTop10ByNameContainingIgnoreCase(filter).map { builderFactory.tags().searchResult(it).build() }
    }

    private fun countTags(filter: String) = when {
        caseSensitiveCheckbox.value -> tagService!!.countByNameContaining(filter).toInt()
        else -> tagService!!.countByNameContainingIgnoreCase(filter).toInt()
    }

    override fun open() {
        super.open()
        val element = searchField.element
        element.executeJs("setTimeout(function() { $0.focus() }, 50)", element)
    }
}