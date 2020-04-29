package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.brain.t
import com.dude.dms.ui.components.misc.IconToggle
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField

class AttributeEditDialog(
        private val attribute: Attribute,
        private val attributeService: AttributeService
) : DmsDialog(t("attribute.edit"), "35vw") {

    private val nameTextField = TextField(t("name"), attribute.name, "").apply {
        setWidthFull()
    }

    private val typeComboBox = ComboBox<Attribute.Type>(t("attribute.type")).apply {
        setWidthFull()
        isPreventInvalidInput = true
        isAllowCustomValue = false
        setItems(*Attribute.Type.values())
        value = attribute.type
    }

    private val requiredToggle = IconToggle(VaadinIcon.LOCK.create(), VaadinIcon.UNLOCK.create(), t("attribute.required"), attribute.isRequired)

    init {
        val fieldWrapper = HorizontalLayout(nameTextField, typeComboBox, requiredToggle).apply {
            setWidthFull()
            alignItems = FlexComponent.Alignment.END
        }
        val createButton = Button(t("save"), VaadinIcon.PLUS.create()) { save() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }
        val cancelButton = Button(t("close"), VaadinIcon.CLOSE.create()) { close() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_ERROR)
        }
        val buttonWrapper = HorizontalLayout(createButton, cancelButton).apply {
            setWidthFull()
        }
        val wrapper = VerticalLayout(fieldWrapper, buttonWrapper).apply {
            setSizeFull()
            isPadding = false
            isSpacing = false
        }
        add(wrapper)
    }

    private fun save() {
        if (nameTextField.isEmpty) {
            return
        }
        attribute.name = nameTextField.value
        attribute.type = typeComboBox.value
        attribute.isRequired = requiredToggle.value
        attributeService.save(attribute)
        close()
    }
}