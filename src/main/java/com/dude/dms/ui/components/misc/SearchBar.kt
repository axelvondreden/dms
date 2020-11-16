package com.dude.dms.ui.components.misc

import com.dude.dms.backend.service.DocService
import com.dude.dms.brain.t
import com.dude.dms.extensions.searchParser
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.wontlost.ckeditor.Constants
import com.wontlost.ckeditor.VaadinCKEditor
import com.wontlost.ckeditor.VaadinCKEditorBuilder


class SearchBar : HorizontalLayout() {

    lateinit var textFilter: VaadinCKEditor
    lateinit var fromFilter: DatePicker
    lateinit var toFilter: DatePicker

    var onChange: (() -> Unit)? = null

    init {
        isPadding = false
        isSpacing = false
        alignItems = FlexComponent.Alignment.STRETCH

        add(VaadinCKEditorBuilder().with { builder: VaadinCKEditorBuilder ->
            builder.editorType = Constants.EditorType.INLINE
            builder.editorData = "Inline"
        }.createVaadinCKEditor())

        horizontalLayout {
            setWidthFull()

            textFilter = VaadinCKEditorBuilder().with {
                with(it) {
                    editorType = Constants.EditorType.DECOUPLED
                    editorData = "Balloon Editor test"
                    width = "100%"
                    //config = Config().apply { setBalloonToolBar(emptyArray()) }
                    //TODO
                    //addValueChangeListener { onChange?.invoke() }
                    //valueChangeMode = ValueChangeMode.LAZY
                }
            }.createVaadinCKEditor()
            //TODO
            add(textFilter)
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
                text = textFilter.optionalValue.orElse(null)
        )

    fun refresh() {
        searchParser.refresh()
    }
}