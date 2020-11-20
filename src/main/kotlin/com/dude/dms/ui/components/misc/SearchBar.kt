package com.dude.dms.ui.components.misc

import com.dude.dms.backend.service.DocService
import com.dude.dms.brain.parsing.search.SearchParser
import com.dude.dms.brain.t
import com.dude.dms.extensions.*
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.listbox.ListBox
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.util.*
import kotlin.concurrent.schedule


class SearchBar : HorizontalLayout() {

    private lateinit var textFilter: TextField
    private lateinit var textTips: ListBox<String>
    private val fromFilter: DatePicker
    private val toFilter: DatePicker
    private lateinit var searchValidatorIcon: Button

    var onChange: (() -> Unit)? = null

    private val searchParser = SearchParser()

    private val viewUI = UI.getCurrent()

    init {
        setWidthFull()

        horizontalLayout(isPadding = false, isSpacing = false) {
            setWidthFull()
            verticalLayout(isPadding = false, isSpacing = false) {
                setWidthFull()
                textFilter = textField {
                    setWidthFull()
                    placeholder = t("search")
                    valueChangeMode = ValueChangeMode.EAGER
                    element.addEventListener("keydown") {
                        Notification.show(it.eventData.asString())
                    }.preventDefault()
                    addValueChangeListener {
                        val error = searchParser.setInput(it.value)
                        textTips.setItems(searchParser.getTips())
                        if (error.isNullOrBlank()) {
                            searchValidatorIcon.icon = VaadinIcon.CHECK.create()
                            searchValidatorIcon.addThemeVariants(ButtonVariant.LUMO_SUCCESS)
                            searchValidatorIcon.removeThemeVariants(ButtonVariant.LUMO_ERROR)
                            searchValidatorIcon.clearTooltips()
                        } else {
                            searchValidatorIcon.icon = VaadinIcon.CLOSE.create()
                            searchValidatorIcon.addThemeVariants(ButtonVariant.LUMO_ERROR)
                            searchValidatorIcon.removeThemeVariants(ButtonVariant.LUMO_SUCCESS)
                            searchValidatorIcon.tooltip(error)
                            Timer().schedule(200) { viewUI.access { searchValidatorIcon.showTooltip() } }
                        }
                    }
                }
                textTips = listBox {
                    setWidthFull()
                    setItems(searchParser.getTips())
                }
            }
            searchValidatorIcon = iconButton(VaadinIcon.CHECK.create()) {
                addThemeVariants(ButtonVariant.LUMO_SUCCESS)
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