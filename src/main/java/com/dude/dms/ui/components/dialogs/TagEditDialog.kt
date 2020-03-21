package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.standard.DmsColorPickerSimple
import com.github.juchar.colorpicker.ColorPickerFieldRaw
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField

class TagEditDialog(
        builderFactory: BuilderFactory,
        private val tag: Tag,
        private val tagService: TagService,
        private val docService: DocService
) : Dialog() {

    private val name = TextField(t("name"), tag.name, "").apply { setWidthFull() }

    @Suppress("UNCHECKED_CAST")
    private val colorPicker = when {
        Options.get().tag.simpleColors -> DmsColorPickerSimple(t("color"))
        else -> ColorPickerFieldRaw(t("color"))
    }.also {
        (it as HasSize).setWidthFull()
        (it as HasValue<*, String>).setValue(tag.color)
    }

    private val attributeSelector = builderFactory.attributes().selector(tag).apply { setSizeFull() }

    init {
        width = "35vw"

        val createButton = Button(t("save")) { save() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }
        val cancelButton = Button(t("close")) { close() }.apply {
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

    private fun save() {
        if (name.isEmpty) return
        if ((colorPicker as HasValue<*, *>).isEmpty()) return
        tag.name = name.value
        tag.color = colorPicker.getValue() as String
        tag.attributes = attributeSelector.selectedAttributes
        tagService.save(tag)
        docService.findByTag(tag).forEach { docService.save(it) }
        close()
    }
}