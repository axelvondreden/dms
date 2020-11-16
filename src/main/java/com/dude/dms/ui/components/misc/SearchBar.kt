package com.dude.dms.ui.components.misc

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.service.DocService
import com.dude.dms.brain.t
import com.dude.dms.extensions.attributeService
import com.dude.dms.extensions.multiSelectComboBox
import com.dude.dms.extensions.radioButtonGroup
import com.dude.dms.extensions.tagService
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.radiobutton.RadioGroupVariant
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import org.springframework.data.domain.Sort
import org.vaadin.gatanaso.MultiselectComboBox

class SearchBar : HorizontalLayout() {

    private val sorts = listOf(
            "${t("date")} ${t("descending")}" to Sort.by(Sort.Direction.DESC, "documentDate"),
            "${t("date")} ${t("ascending")}" to Sort.by(Sort.Direction.ASC, "documentDate"),
            "${t("created")} ${t("descending")}" to Sort.by(Sort.Direction.DESC, "insertTime"),
            "${t("created")} ${t("ascending")}" to Sort.by(Sort.Direction.ASC, "insertTime")
    )

    lateinit var tagIncludeFilter: MultiselectComboBox<Tag>
    lateinit var tagIncludeVariant: RadioButtonGroup<String>
    lateinit var tagExcludeFilter: MultiselectComboBox<Tag>

    lateinit var attributeIncludeFilter: MultiselectComboBox<Attribute>
    lateinit var attributeIncludeVariant: RadioButtonGroup<String>
    lateinit var attributeExcludeFilter: MultiselectComboBox<Attribute>

    lateinit var textFilter: TextField
    lateinit var fromFilter: DatePicker
    lateinit var toFilter: DatePicker

    private lateinit var sortFilter: ComboBox<Pair<String, Sort>>
    var onChange: (() -> Unit)? = null

    init {
        isPadding = false
        isSpacing = false
        alignItems = FlexComponent.Alignment.STRETCH

        horizontalLayout {
            setWidthFull()

            textFilter = textField {
                setWidthFull()
                placeholder = "Text"
                isClearButtonVisible = true
                addValueChangeListener { onChange?.invoke()}
                valueChangeMode = ValueChangeMode.LAZY
            }
            fromFilter = datePicker {
                placeholder = t("from")
                addValueChangeListener { onChange?.invoke()}
                isClearButtonVisible = true
            }
            toFilter = datePicker {
                placeholder = t("to")
                addValueChangeListener { onChange?.invoke()}
                isClearButtonVisible = true
            }
            sortFilter = comboBox {
                setItems(sorts)
                isPreventInvalidInput = true
                isAllowCustomValue = false
                value = sorts[0]
                setItemLabelGenerator { it.first }
                addValueChangeListener { onChange?.invoke()}
            }
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
                            addValueChangeListener { onChange?.invoke()}
                        }
                        tagIncludeFilter = multiSelectComboBox("", tagService.findAll()) {
                            width = "20vw"
                            maxWidth = "20vw"
                            placeholder = t("tags")
                            isClearButtonVisible = true
                            isAllowCustomValues = false
                            setItemLabelGenerator { it.name }
                            addValueChangeListener { onChange?.invoke()}
                        }
                        tagExcludeFilter = multiSelectComboBox("", tagService.findAll()) {
                            width = "20vw"
                            maxWidth = "20vw"
                            placeholder = t("tags.exclude")
                            isClearButtonVisible = true
                            isAllowCustomValues = false
                            setItemLabelGenerator { it.name }
                            addValueChangeListener { onChange?.invoke()}
                        }
                    }
                    horizontalLayout {
                        setWidthFull()
                        alignItems = FlexComponent.Alignment.CENTER

                        attributeIncludeVariant = radioButtonGroup {
                            setItems(t("all"), t("any"))
                            addThemeVariants(RadioGroupVariant.LUMO_VERTICAL)
                            value = t("all")
                            addValueChangeListener { onChange?.invoke()}
                        }
                        attributeIncludeFilter = multiSelectComboBox("", attributeService.findAll()) {
                            width = "20vw"
                            maxWidth = "20vw"
                            placeholder = t("attributes")
                            isClearButtonVisible = true
                            isAllowCustomValues = false
                            setItemLabelGenerator { it.name }
                            addValueChangeListener { onChange?.invoke()}
                        }
                        attributeExcludeFilter = multiSelectComboBox("", attributeService.findAll()) {
                            width = "20vw"
                            maxWidth = "20vw"
                            placeholder = t("attributes.exclude")
                            isClearButtonVisible = true
                            isAllowCustomValues = false
                            setItemLabelGenerator { it.name }
                            addValueChangeListener { onChange?.invoke()}
                        }
                    }
                }
            }
        }
    }

    val sort get() = sortFilter.value.second

    val filter get() = DocService.Filter(
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

    fun refresh() {
        tagIncludeFilter.setItems(tagService.findAll())
        attributeIncludeFilter.setItems(attributeService.findAll())
    }
}