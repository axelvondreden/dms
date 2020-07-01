package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.github.juchar.colorpicker.ColorPickerFieldRaw
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField

class TagCreateDialog(builderFactory: BuilderFactory, private val tagService: TagService) : DmsDialog(t("tag.create"), "35vw") {

    private val name = TextField(t("name")).apply { setWidthFull() }

    private val colorPicker = ColorPickerFieldRaw(t("color")).apply { setWidthFull() }

    private val attributeSelector = builderFactory.attributes().selector().apply { setSizeFull() }

    init {
        val createButton = Button(t("create"), VaadinIcon.PLUS.create()) { create() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }
        val cancelButton = Button(t("close"), VaadinIcon.CLOSE.create()) { close() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_ERROR)
        }
        val fieldWrapper = HorizontalLayout(name, colorPicker as Component).apply { setWidthFull() }
        val buttonLayout = HorizontalLayout(createButton, cancelButton).apply { setWidthFull() }
        val attributeDetails = Details(t("attributes"), attributeSelector).apply { element.style["width"] = "100%" }
        val vLayout = VerticalLayout(fieldWrapper, attributeDetails, buttonLayout).apply {
            setSizeFull()
            isPadding = false
            isSpacing = false
        }
        add(vLayout)
    }

    private fun create() {
        if (name.isEmpty) return
        if ((colorPicker as HasValue<*, *>).isEmpty()) return
        if (tagService.findByName(name.value) != null) return
        val tag = Tag(name.value, colorPicker.value as String)
        tagService.create(tag)
        tag.attributes = attributeSelector.selectedAttributes
        tagService.save(tag)
        close()
    }
}