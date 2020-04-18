package com.dude.dms.ui.components.tags

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.service.AttributeValueService
import com.dude.dms.brain.extensions.convert
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.NumberField
import com.vaadin.flow.component.textfield.TextField

class AttributeValueField(attributeValue: AttributeValue, attributeValueService: AttributeValueService, readOnly: Boolean = false) : HorizontalLayout() {

    private val validation: () -> Boolean

    private val isRequired = attributeValue.attribute.isRequired

    val label = attributeValue.attribute.name + if (isRequired) " *" else ""

    var onChange: (() -> Unit)? = null

    init {
        when (attributeValue.attribute.type) {
            Attribute.Type.STRING -> {
                if (readOnly) {
                    add(Label("$label: ${attributeValue.stringValue}"))
                    validation = { true }
                } else {
                    val textField = TextField(label).apply {
                        value = attributeValue.stringValue ?: ""
                        addValueChangeListener { event ->
                            if (!event.hasValue.isEmpty() || !isRequired) {
                                attributeValue.stringValue = event.value
                                if (attributeValue.doc != null) attributeValueService.save(attributeValue)
                                onChange?.invoke()
                            }
                        }
                        setWidthFull()
                    }
                    add(textField)
                    validation = { !isRequired || !textField.isEmpty }
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
                }
            }
        }
    }

    fun validate() = validation.invoke()
}