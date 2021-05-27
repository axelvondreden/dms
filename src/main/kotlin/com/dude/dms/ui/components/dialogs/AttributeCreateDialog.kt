package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.brain.t
import com.dude.dms.ui.views.AttributeView
import com.dude.dms.utils.attributeService
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon


class AttributeCreateDialog : DmsDialog(t("attribute.create"), 35) {

    init {
        verticalLayout(isPadding = false, isSpacing = false) {
            setSizeFull()

            val nameTextField = textField(t("name")) { setWidthFull() }
            val typeComboBox = comboBox<Attribute.Type>(t("attribute.type")) {
                setWidthFull()
                isPreventInvalidInput = true
                isAllowCustomValue = false
                setItems(*Attribute.Type.values())
                value = Attribute.Type.STRING
            }
            val requiredToggle = checkBox(t("attribute.required"))
            horizontalLayout {
                setWidthFull()

                button(t("create"), VaadinIcon.PLUS.create()) {
                    setWidthFull()
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                    onLeftClick {
                        if (!nameTextField.isEmpty) {
                            val attribute = attributeService.create(Attribute(nameTextField.value, requiredToggle.value, typeComboBox.value))
                            close()
                            UI.getCurrent().navigate(AttributeView::class.java, attribute.id.toString())
                        }
                    }
                }
                button(t("close"), VaadinIcon.CLOSE.create()) {
                    setWidthFull()
                    addThemeVariants(ButtonVariant.LUMO_ERROR)
                    onLeftClick { close() }
                }
            }
        }
    }
}
