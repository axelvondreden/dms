package com.dude.dms.ui.components.misc

import com.dude.dms.backend.service.DocService
import com.dude.dms.brain.parsing.search.SearchParser
import com.dude.dms.brain.t
import com.dude.dms.extensions.attributeService
import com.dude.dms.extensions.autocomplete
import com.dude.dms.extensions.tagService
import com.github.mvysny.karibudsl.v10.datePicker
import com.vaadin.componentfactory.Autocomplete
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.orderedlayout.HorizontalLayout


class SearchBar : HorizontalLayout() {

    private var textFilter: Autocomplete
    private var fromFilter: DatePicker
    private var toFilter: DatePicker

    var onChange: (() -> Unit)? = null

    private val searchParser = SearchParser(tagService, attributeService)

    init {
        setWidthFull()

        textFilter = autocomplete(10) {
            setWidthFull()
            setPlaceholder(t("search"))
            options = searchParser.getTips()
            addValueChangeListener {
                searchParser.setInput(it.value)
                options = searchParser.getTips()
            }
        }
        fromFilter = datePicker {
            placeholder = t("from")
            addValueChangeListener { onChange?.invoke() }
            isClearButtonVisible = true
        }
        toFilter = datePicker {
            placeholder = t("to")
            addValueChangeListener { onChange?.invoke() }
            isClearButtonVisible = true
        }
    }

    val filter
        get() = DocService.Filter(
                true, true,
                /*includeAllTags = t("all") == tagIncludeVariant.value,
                includeAllAttributes = t("all") == attributeIncludeVariant.value,
                includedTags = tagIncludeFilter.optionalValue.orElse(null),
                excludedTags = tagExcludeFilter.optionalValue.orElse(null),
                includedAttributes = attributeIncludeFilter.optionalValue.orElse(null),
                excludedAttributes = attributeExcludeFilter.optionalValue.orElse(null),*/
                from = fromFilter.optionalValue.orElse(null),
                to = toFilter.optionalValue.orElse(null),
                //text = textFilter.optionalValue.orElse(null)
        )

    fun refresh() {
        searchParser.refresh()
    }
}