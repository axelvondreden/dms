package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.Tag
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.t
import com.dude.dms.utils.attributeSelector
import com.dude.dms.utils.colorPicker
import com.dude.dms.utils.tagService
import com.dude.dms.ui.components.tags.AttributeSelector
import com.github.juchar.colorpicker.ColorPickerFieldRaw
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextField

class TagCreateDialog : DmsDialog(t("tag.create"), 35) {

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

                content { attributeSelector = attributeSelector { setSizeFull() } }
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
        if (name.isEmpty) {
            LOGGER.showError(t("name.missing"), UI.getCurrent())
            return
        }
        if (colorPicker.isEmpty) {
            LOGGER.showError(t("color.missing"), UI.getCurrent())
            return
        }
        if (tagService.findByName(name.value) != null) {
            LOGGER.showError(t("tag.exists"), UI.getCurrent())
            return
        }
        val tag = Tag(name.value, colorPicker.value as String)
        tagService.create(tag)
        tag.attributes = attributeSelector.selectedAttributes
        tagService.save(tag)
        close()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(TagCreateDialog::class.java)
    }
}