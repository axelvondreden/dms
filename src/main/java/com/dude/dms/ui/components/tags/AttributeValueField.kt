package com.dude.dms.ui.components.tags

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.AttributeValueService
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.NumberField
import com.vaadin.flow.component.textfield.TextField

class AttributeValueField(
        attributeValue: AttributeValue,
        attributeService: AttributeService,
        attributeValueService: AttributeValueService
) : HorizontalLayout() {

    private val validation: () -> Boolean

    private val attribute = attributeService.findByAttributeValue(attributeValue)

    val label = attribute.name

    private val isRequired = attribute.isRequired

    init {
        when (attribute.type) {
            Attribute.Type.STRING -> {
                val textField = TextField(label).apply {
                    value = attributeValue.stringValue ?: ""
                    addValueChangeListener { event ->
                        if (!event.hasValue.isEmpty() || !isRequired) {
                            attributeValue.stringValue = event.value
                            attributeValueService.save(attributeValue)
                        }
                    }
                    setWidthFull()
                }
                add(textField)
                validation = { !isRequired || !textField.isEmpty }
            }
            Attribute.Type.INT -> {
                val intField = NumberField(label).apply {
                    step = 1.0
                    value = attributeValue.intValue?.toDouble()
                    addValueChangeListener { event ->
                        if (!event.hasValue.isEmpty() || !isRequired) {
                            attributeValue.intValue = event.value.toInt()
                            attributeValueService.save(attributeValue)
                        }
                    }
                    setWidthFull()
                }
                add(intField)
                validation = { !isRequired || !intField.isEmpty }
            }
            Attribute.Type.FLOAT -> {
                val floatField = NumberField(label).apply {
                    step = 0.01
                    value = attributeValue.floatValue?.toDouble()
                    addValueChangeListener { event ->
                        if (!event.hasValue.isEmpty() || !isRequired) {
                            attributeValue.floatValue = event.value.toFloat()
                            attributeValueService.save(attributeValue)
                        }
                    }
                    setWidthFull()
                }
                add(floatField)
                validation = { !isRequired || !floatField.isEmpty }
            }
            Attribute.Type.DATE -> {
                val datePicker = DatePicker(label).apply {
                    attributeValue.dateValue?.let { value = it }
                    addValueChangeListener { event ->
                        if (!event.hasValue.isEmpty() || !isRequired) {
                            attributeValue.dateValue = event.value
                            attributeValueService.save(attributeValue)
                        }
                    }
                    setWidthFull()
                }
                add(datePicker)
                validation = { !isRequired || !datePicker.isEmpty }
            }
        }
    }

    fun validate() = validation.invoke()
}