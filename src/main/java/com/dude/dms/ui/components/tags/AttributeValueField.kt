package com.dude.dms.ui.components.tags

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.service.AttributeValueService
import com.dude.dms.extensions.convert
import com.dude.dms.extensions.findDate
import com.dude.dms.ui.components.misc.DocImageEditor
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.NumberField

class AttributeValueField(
        attributeValue: AttributeValue,
        attributeValueService: AttributeValueService,
        readOnly: Boolean = false,
        imageEditor: DocImageEditor? = null
) : HorizontalLayout() {

    private val validation: () -> Boolean

    private val isRequired = attributeValue.attribute.isRequired

    val label = attributeValue.attribute.name + if (isRequired && !readOnly) " *" else ""

    var onChange: (() -> Unit)? = null

    init {
        when (attributeValue.attribute.type) {
            Attribute.Type.STRING -> {
                if (readOnly) {
                    add(Label("$label: ${attributeValue.stringValue}"))
                    validation = { true }
                } else {
                    val textField = ComboBox<String>(label).apply {
                        isAllowCustomValue = true
                        setItems(attributeValueService.findAutocomplete(attributeValue.attribute))
                        value = attributeValue.stringValue ?: ""
                        addValueChangeListener { event ->
                            if (!event.hasValue.isEmpty() || !isRequired) {
                                attributeValue.stringValue = event.value
                                if (attributeValue.doc != null) attributeValueService.save(attributeValue)
                                onChange?.invoke()
                            }
                        }
                        addCustomValueSetListener { value = it.detail }
                        setWidthFull()
                    }
                    add(textField)
                    validation = { !isRequired || (!textField.isEmpty && !textField.value.isNullOrBlank()) }
                    if (imageEditor != null) {
                        val pick = Button(VaadinIcon.CROSSHAIRS.create()) { event ->
                            imageEditor.pickEvent = {
                                textField.value = it?.word?.text ?: textField.value
                                event.source.removeThemeVariants(ButtonVariant.LUMO_SUCCESS)
                            }
                            event.source.addThemeVariants(ButtonVariant.LUMO_SUCCESS)
                        }
                        add(pick)
                    }
                }
            }
            Attribute.Type.INT -> {
                if (readOnly) {
                    add(Label("$label: ${attributeValue.intValue}"))
                    validation = { true }
                } else {
                    val intField = NumberField(label).apply {
                        step = 1.0
                        value = attributeValue.intValue?.toDouble()
                        addValueChangeListener { event ->
                            if (!event.hasValue.isEmpty() || !isRequired) {
                                attributeValue.intValue = event.value.toInt()
                                if (attributeValue.doc != null) attributeValueService.save(attributeValue)
                                onChange?.invoke()
                            }
                        }
                        setWidthFull()
                    }
                    add(intField)
                    validation = { !isRequired || !intField.isEmpty }
                    if (imageEditor != null) {
                        val pick = Button(VaadinIcon.CROSSHAIRS.create()) { event ->
                            imageEditor.pickEvent = {
                                intField.value = it?.word?.text?.toDoubleOrNull() ?: intField.value
                                event.source.removeThemeVariants(ButtonVariant.LUMO_SUCCESS)
                            }
                            event.source.addThemeVariants(ButtonVariant.LUMO_SUCCESS)
                        }
                        add(pick)
                    }
                }
            }
            Attribute.Type.FLOAT -> {
                if (readOnly) {
                    add(Label("$label: ${attributeValue.floatValue}"))
                    validation = { true }
                } else {
                    val floatField = NumberField(label).apply {
                        step = 0.01
                        value = attributeValue.floatValue?.toDouble()
                        addValueChangeListener { event ->
                            if (!event.hasValue.isEmpty() || !isRequired) {
                                attributeValue.floatValue = event.value.toFloat()
                                if (attributeValue.doc != null) attributeValueService.save(attributeValue)
                                onChange?.invoke()
                            }
                        }
                        setWidthFull()
                    }
                    add(floatField)
                    validation = { !isRequired || !floatField.isEmpty }
                    if (imageEditor != null) {
                        val pick = Button(VaadinIcon.CROSSHAIRS.create()) { event ->
                            imageEditor.pickEvent = {
                                floatField.value = it?.word?.text?.toDoubleOrNull() ?: floatField.value
                                event.source.removeThemeVariants(ButtonVariant.LUMO_SUCCESS)
                            }
                            event.source.addThemeVariants(ButtonVariant.LUMO_SUCCESS)
                        }
                        add(pick)
                    }
                }
            }
            Attribute.Type.DATE -> {
                if (readOnly) {
                    add(Label("$label: ${attributeValue.dateValue?.convert()}"))
                    validation = { true }
                } else {
                    val datePicker = DatePicker(label).apply {
                        attributeValue.dateValue?.let { value = it }
                        addValueChangeListener { event ->
                            if (!event.hasValue.isEmpty() || !isRequired) {
                                attributeValue.dateValue = event.value
                                if (attributeValue.doc != null) attributeValueService.save(attributeValue)
                                onChange?.invoke()
                            }
                        }
                        setWidthFull()
                    }
                    add(datePicker)
                    validation = { !isRequired || !datePicker.isEmpty }
                    if (imageEditor != null) {
                        val pick = Button(VaadinIcon.CROSSHAIRS.create()) { event ->
                            imageEditor.pickEvent = {
                                datePicker.value = it?.word?.text?.findDate() ?: datePicker.value
                                event.source.removeThemeVariants(ButtonVariant.LUMO_SUCCESS)
                            }
                            event.source.addThemeVariants(ButtonVariant.LUMO_SUCCESS)
                        }
                        add(pick)
                    }
                }
            }
        }
    }

    fun validate() = validation.invoke()
}