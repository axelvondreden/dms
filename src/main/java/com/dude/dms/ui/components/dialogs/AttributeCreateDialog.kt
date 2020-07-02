package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.brain.t
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.TextField


class AttributeCreateDialog(private val attributeService: AttributeService) : DmsDialog(t("attribute.create"), 35) {

    private lateinit var nameTextField: TextField

    private lateinit var typeComboBox: ComboBox<Attribute.Type>

    private lateinit var requiredToggle: Checkbox

    init {
        verticalLayout(isPadding = false, isSpacing = false) {
            setSizeFull()

            horizontalLayout {
                setWidthFull()
                alignItems = FlexComponent.Alignment.END

                nameTextField = textField(t("name")) { setWidthFull() }
                typeComboBox = comboBox(t("attribute.type")) {
                    setWidthFull()
                    isPreventInvalidInput = true
                    isAllowCustomValue = false
                    setItems(*Attribute.Type.values())
                    value = Attribute.Type.STRING
                }
                requiredToggle = checkBox(t("attribute.required"))
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
        if (nameTextField.isEmpty) {
            return
        }
        val attribute = Attribute(nameTextField.value, requiredToggle.value, typeComboBox.value)
        attributeService.create(attribute)
        close()
    }
}