package com.dude.dms.ui.components.misc

import com.dude.dms.backend.service.DocService
import com.dude.dms.brain.t
import com.dude.dms.extensions.searchParser
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField


class SearchBar : HorizontalLayout() {

    private var textFilter: TextField
    private var fromFilter: DatePicker
    private var toFilter: DatePicker

    var onChange: (() -> Unit)? = null

    init {
        setWidthFull()

        textFilter = textField {
            placeholder = t("search")
            setWidthFull()
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