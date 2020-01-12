package com.dude.dms.ui.components.tags

import com.dude.dms.backend.brain.DmsLogger
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.service.AttributeValueService
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.NumberField
import com.vaadin.flow.component.textfield.TextField

class AttributeValueField(attributeValue: AttributeValue, attributeValueService: AttributeValueService) : HorizontalLayout() {

    private val validation: () -> Boolean

    val label = attributeValue.attribute.name

    val isRequired = attributeValue.attribute.isRequired

    init {
        when (attributeValue.attribute.type) {
            Attribute.Type.STRING -> {
                val textField = TextField(label).apply {
                    value = attributeValue.stringValue ?: ""
                    setWidthFull()
                    addValueChangeListener { event ->
                        if (event.hasValue.isEmpty() && isRequired) {
                            LOGGER.showError("Attribute '$label' can not be empty.")
                        } else {
                            attributeValue.stringValue = event.value
                            attributeValueService.save(attributeValue)
                            LOGGER.showInfo("Saved '$label'!")
                        }
                    }
                }
                add(textField)
                validation = { !isRequired || !textField.isEmpty }
            }
            Attribute.Type.INT -> {
                val intField = NumberField(label).apply {
                    step = 1.0
                    value = attributeValue.intValue?.toDouble()
                    setWidthFull()
                    addValueChangeListener { event ->
                        if (event.hasValue.isEmpty() && isRequired) {
                            LOGGER.showError("Attribute '$label' can not be empty.")
                        } else {
                            attributeValue.intValue = event.value.toInt()
                            attributeValueService.save(attributeValue)
                            LOGGER.showInfo("Saved '$label'!")
                        }
                    }
                }
                add(intField)
                validation = { !isRequired || !intField.isEmpty }
            }
            Attribute.Type.FLOAT -> {
                val floatField = NumberField(label).apply {
                    step = 0.01
                    value = attributeValue.floatValue?.toDouble()
                    setWidthFull()
                    addValueChangeListener { event ->
                        if (event.hasValue.isEmpty() && isRequired) {
                            LOGGER.showError("Attribute '$label' can not be empty.")
                        } else {
                            attributeValue.floatValue = event.value.toFloat()
                            attributeValueService.save(attributeValue)
                            LOGGER.showInfo("Saved '$label'!")
                        }
                    }
                }
                add(floatField)
                validation = { !isRequired || !floatField.isEmpty }
            }
            Attribute.Type.DATE -> {
                val datePicker = DatePicker(label).apply {
                    attributeValue.dateValue?.let { value = it }
                    setWidthFull()
                    addValueChangeListener { event ->
                        if (event.hasValue.isEmpty() && isRequired) {
                            LOGGER.showError("Attribute '$label' can not be empty.")
                        } else {
                            attributeValue.dateValue = event.value
                            attributeValueService.save(attributeValue)
                            LOGGER.showInfo("Saved '$label'!")
                        }
                    }
                }
                add(datePicker)
                validation = { !isRequired || !datePicker.isEmpty }
            }
        }
    }

    fun validate() = validation.invoke()

    companion object {
        private val LOGGER = DmsLogger.getLogger(AttributeValueField::class.java)
    }
}