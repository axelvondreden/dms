package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.Tag
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.t
import com.dude.dms.utils.attributeSelector
import com.dude.dms.utils.colorPicker
import com.dude.dms.utils.docService
import com.dude.dms.utils.tagService
import com.dude.dms.ui.components.tags.AttributeSelector
import com.github.juchar.colorpicker.ColorPickerFieldRaw
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.textfield.TextField

class TagEditDialog(private val tag: Tag) : DmsDialog(t("tag.edit"), 35) {

    private lateinit var name: TextField

    private lateinit var colorPicker: ColorPickerFieldRaw

    private lateinit var attributeSelector: AttributeSelector

    init {
        verticalLayout(isPadding = false, isSpacing = false) {
            setSizeFull()

            horizontalLayout {
                setWidthFull()

                name = textField(t("name")) {
                    setWidthFull()
                    value = tag.name
                }
                colorPicker = colorPicker(t("color")) {
                    setWidthFull()
                    value = tag.color
                }
            }
            details(t("attributes")) {
                element.style["width"] = "100%"

                content {
                    attributeSelector = attributeSelector {
                        setSizeFull()
                        selectedAttributes = tag.attributes
                    }
                }
            }
            horizontalLayout {
                setWidthFull()

                button(t("save")) {
                    onLeftClick { save() }
                    setWidthFull()
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                }
                button(t("close")) {
                    onLeftClick { close() }
                    setWidthFull()
                    addThemeVariants(ButtonVariant.LUMO_ERROR)
                }
            }
        }
    }

    private fun save() {
        if (name.isEmpty) {
            LOGGER.showError(t("name.missing"), UI.getCurrent())
            return
        }
        if (colorPicker.isEmpty) {
            LOGGER.showError(t("color.missing"), UI.getCurrent())
            return
        }
        tag.name = name.value
        tag.color = colorPicker.value as String
        tag.attributes = attributeSelector.selectedAttributes
        tagService.save(tag)
        docService.findByTag(tag).forEach { docService.save(it) }
        close()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(TagEditDialog::class.java)
    }
}