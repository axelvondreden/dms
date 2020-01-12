package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.ui.components.misc.IconToggle
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField

class AttributeCreateDialog(private val attributeService: AttributeService) : EventDialog<Attribute>() {

    private val nameTextField = TextField("Name").apply {
        setWidthFull()
    }

    private val typeComboBox = ComboBox<Attribute.Type>("Type").apply {
        setWidthFull()
        isPreventInvalidInput = true
        isAllowCustomValue = false
        setItems(*Attribute.Type.values())
        value = Attribute.Type.STRING
    }

    private val requiredToggle = IconToggle(VaadinIcon.LOCK.create(), VaadinIcon.UNLOCK.create(), "Required")

    init {
        width = "35vw"
        val fieldWrapper = HorizontalLayout(nameTextField, typeComboBox, requiredToggle).apply {
            setWidthFull()
            alignItems = FlexComponent.Alignment.END
        }
        val createButton = Button("Create", VaadinIcon.PLUS.create()) { create() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }
        val cancelButton = Button("Close", VaadinIcon.CLOSE.create()) { close() }.apply {
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

    private fun create() {
        if (nameTextField.isEmpty) {
            nameTextField.errorMessage = "Name can not be empty!"
            return
        }
        val attribute = Attribute(nameTextField.value, requiredToggle.value, typeComboBox.value)
        attributeService.create(attribute)
        triggerCreateEvent(attribute)
        close()
    }
}