package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.t
import com.dude.dms.extensions.attributeSelector
import com.dude.dms.extensions.colorPicker
import com.dude.dms.ui.components.tags.AttributeSelector
import com.github.juchar.colorpicker.ColorPickerFieldRaw
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextField

class TagCreateDialog(
        private val tagService: TagService,
        attributeService: AttributeService,
        eventManager: EventManager
) : DmsDialog(t("tag.create"), 35) {

    private lateinit var name: TextField

    private lateinit var colorPicker: ColorPickerFieldRaw

    private lateinit var attributeSelector: AttributeSelector

    init {
        verticalLayout(isPadding = false, isSpacing = false) {
            setSizeFull()

            horizontalLayout {
                setWidthFull()

                name = textField(t("name")) { setWidthFull() }
                colorPicker = colorPicker(t("color")) { setWidthFull() }
            }
            details(t("attributes")) {
                element.style["width"] = "100%"

                content { attributeSelector = attributeSelector(attributeService, eventManager) { setSizeFull() } }
            }
            horizontalLayout {
                setWidthFull()

                button(t("create"), VaadinIcon.PLUS.create()) {
                    onLeftClick { create() }
                    setWidthFull()
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                }
                button(t("close"), VaadinIcon.CLOSE.create()) {
                    onLeftClick { close() }
                    setWidthFull()
                    addThemeVariants(ButtonVariant.LUMO_ERROR)
                }
            }
        }
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