package com.dude.dms.ui.components.tags

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.extensions.attributeValueService
import com.dude.dms.extensions.findDate
import com.dude.dms.extensions.findDecimal
import com.dude.dms.extensions.round
import com.dude.dms.ui.components.misc.DocImageEditor
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.NumberField

class AttributeValueField(val attributeValue: AttributeValue, imageEditor: DocImageEditor? = null) : HorizontalLayout() {

    private val validation: () -> Boolean

    private val isRequired = attributeValue.attribute.isRequired

    val label = attributeValue.attribute.name + if (isRequired) " *" else ""

    private val textField
        get() = ComboBox<String>(label).apply {
            isAllowCustomValue = true
            val items = attributeValueService.findAutocomplete(attributeValue.attribute)
            if (items.isNotEmpty()) {
                setItems()
            }
            value = attributeValue.stringValue ?: ""
            addValueChangeListener { event ->
                if (!event.hasValue.isEmpty || !isRequired) {
                    attributeValue.stringValue = event.value
                    if (attributeValue.doc != null) attributeValueService.save(attributeValue)
                }
            }
            addCustomValueSetListener { value = it.detail }
            setWidthFull()
        }

    private val intField
        get() = NumberField(label).apply {
            step = 1.0
            value = attributeValue.intValue?.toDouble()
            addValueChangeListener { event ->
                if (!event.hasValue.isEmpty || !isRequired) {
                    attributeValue.intValue = event.value.toInt()
                    if (attributeValue.doc != null) attributeValueService.save(attributeValue)
                }
            }
            setWidthFull()
        }

    private val floatField
        get() = NumberField(label).apply {
            step = 0.01
            value = attributeValue.floatValue?.toDouble()?.round(2)
            addValueChangeListener { event ->
                if (!event.hasValue.isEmpty || !isRequired) {
                    attributeValue.floatValue = event.value.toFloat()
                    if (attributeValue.doc != null) attributeValueService.save(attributeValue)
                }
            }
            setWidthFull()
        }

    private val datePicker
        get() = DatePicker(label).apply {
            attributeValue.dateValue?.let { value = it }
            addValueChangeListener { event ->
                if (!event.hasValue.isEmpty || !isRequired) {
                    attributeValue.dateValue = event.value
                    if (attributeValue.doc != null) attributeValueService.save(attributeValue)
                }
            }
            setWidthFull()
        }

    init {
        isSpacing = false
        when (attributeValue.attribute.type) {
            Attribute.Type.STRING -> {
                val field = textField
                add(field)
                validation = { !isRequired || (!field.isEmpty && !field.value.isNullOrBlank()) }
                if (imageEditor != null) {
                    val pick = Button(VaadinIcon.CROSSHAIRS.create()) { event ->
                        imageEditor.pickEvent = {
                            field.value = it?.word?.text ?: field.value
                            event.source.removeThemeVariants(ButtonVariant.LUMO_SUCCESS)
                        }
                        event.source.addThemeVariants(ButtonVariant.LUMO_SUCCESS)
                    }.apply { style["margin"] = "auto auto 5px auto" }
                    add(pick)
                }
            }
            Attribute.Type.INT -> {
                val field = intField
                add(field)
                validation = { !isRequired || !field.isEmpty }
                if (imageEditor != null) {
                    val pick = Button(VaadinIcon.CROSSHAIRS.create()) { event ->
                        imageEditor.pickEvent = {
                            field.value = it?.word?.text?.toDoubleOrNull() ?: field.value
                            event.source.removeThemeVariants(ButtonVariant.LUMO_SUCCESS)
                        }
                        event.source.addThemeVariants(ButtonVariant.LUMO_SUCCESS)
                    }.apply { style["margin"] = "auto auto 5px auto" }
                    add(pick)
                }
            }
            Attribute.Type.FLOAT -> {
                val field = floatField
                add(field)
                validation = { !isRequired || !field.isEmpty }
                if (imageEditor != null) {
                    val pick = Button(VaadinIcon.CROSSHAIRS.create()) { event ->
                        imageEditor.pickEvent = {
                            field.value = it?.word?.text?.findDecimal() ?: field.value
                            event.source.removeThemeVariants(ButtonVariant.LUMO_SUCCESS)
                        }
                        event.source.addThemeVariants(ButtonVariant.LUMO_SUCCESS)
                    }.apply { style["margin"] = "auto auto 5px auto" }
                    add(pick)
                }
            }
            Attribute.Type.DATE -> {
                val field = datePicker
                add(field)
                validation = { !isRequired || !field.isEmpty }
                if (imageEditor != null) {
                    val pick = Button(VaadinIcon.CROSSHAIRS.create()) { event ->
                        imageEditor.pickEvent = {
                            field.value = it?.word?.text?.findDate() ?: field.value
                            event.source.removeThemeVariants(ButtonVariant.LUMO_SUCCESS)
                        }
                        event.source.addThemeVariants(ButtonVariant.LUMO_SUCCESS)
                    }.apply { style["margin"] = "auto auto 5px auto" }
                    add(pick)
                }
            }
        }
    }

    fun validate() = validation.invoke()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttributeValueField
        if (attributeValue != other.attributeValue) return false
        return true
    }

    override fun hashCode() = attributeValue.hashCode()
}