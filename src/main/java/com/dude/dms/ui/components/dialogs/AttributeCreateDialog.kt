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


class AttributeCreateDialog(private val attributeService: AttributeService) : DmsDialog(t("attribute.create"), "35vw") {

    private val nameTextField = TextField(t("name")).apply {
        setWidthFull()
    }

    private val typeComboBox = ComboBox<Attribute.Type>(t("attribute.type")).apply {
        setWidthFull()
        isPreventInvalidInput = true
        isAllowCustomValue = false
        setItems(*Attribute.Type.values())
        value = Attribute.Type.STRING
    }

    private val requiredToggle = IconToggle(VaadinIcon.LOCK.create(), VaadinIcon.UNLOCK.create(), t("attribute.required"))

    init {
        val fieldWrapper = HorizontalLayout(nameTextField, typeComboBox, requiredToggle).apply {
            setWidthFull()
            alignItems = FlexComponent.Alignment.END
        }
        val createButton = Button(t("create"), VaadinIcon.PLUS.create()) { create() }.apply {
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

    private fun create() {
        if (nameTextField.isEmpty) {
            return
        }
        val attribute = Attribute(nameTextField.value, requiredToggle.value, typeComboBox.value)
        attributeService.create(attribute)
        close()
    }
}