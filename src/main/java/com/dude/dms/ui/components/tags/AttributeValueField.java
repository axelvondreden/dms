package com.dude.dms.ui.components.tags;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.docs.AttributeValue;
import com.dude.dms.backend.service.AttributeValueService;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

public class AttributeValueField extends HorizontalLayout {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(AttributeValueField.class);

    public AttributeValueField(AttributeValue attributeValue, AttributeValueService attributeValueService) {
        switch (attributeValue.getAttribute().getType()) {
            case STRING:
                TextField textField = new TextField(attributeValue.getAttribute().getName());
                if (attributeValue.getStringValue() != null) {
                    textField.setValue(attributeValue.getStringValue());
                }
                textField.setWidthFull();
                textField.addValueChangeListener(event -> {
                    if (event.getHasValue().isEmpty() && attributeValue.getAttribute().isRequired()) {
                        LOGGER.showError("Attribute '" + attributeValue.getAttribute().getName() + "' can not be empty.");
                    } else {
                        attributeValue.setStringValue(event.getValue());
                        attributeValueService.save(attributeValue);
                        LOGGER.showInfo("Saved '" + attributeValue.getAttribute().getName() + "'!");
                    }
                });
                add(textField);
                break;
            case INT:
                NumberField intField = new NumberField(attributeValue.getAttribute().getName());
                intField.setStep(1);
                if (attributeValue.getIntValue() != null) {
                    intField.setValue(Double.valueOf(attributeValue.getIntValue()));
                }
                intField.setWidthFull();
                intField.addValueChangeListener(event -> {
                    if (event.getHasValue().isEmpty() && attributeValue.getAttribute().isRequired()) {
                        LOGGER.showError("Attribute '" + attributeValue.getAttribute().getName() + "' can not be empty.");
                    } else {
                        attributeValue.setIntValue(event.getValue().intValue());
                        attributeValueService.save(attributeValue);
                        LOGGER.showInfo("Saved '" + attributeValue.getAttribute().getName() + "'!");
                    }
                });
                add(intField);
                break;
            case FLOAT:
                NumberField floatField = new NumberField(attributeValue.getAttribute().getName());
                floatField.setStep(0.01);
                if (attributeValue.getFloatValue() != null) {
                    floatField.setValue(Double.valueOf(attributeValue.getFloatValue()));
                }
                floatField.setWidthFull();
                floatField.addValueChangeListener(event -> {
                    if (event.getHasValue().isEmpty() && attributeValue.getAttribute().isRequired()) {
                        LOGGER.showError("Attribute '" + attributeValue.getAttribute().getName() + "' can not be empty.");
                    } else {
                        attributeValue.setFloatValue(event.getValue().floatValue());
                        attributeValueService.save(attributeValue);
                        LOGGER.showInfo("Saved '" + attributeValue.getAttribute().getName() + "'!");
                    }
                });
                add(floatField);
                break;
            case DATE:
                DatePicker datePicker = new DatePicker(attributeValue.getAttribute().getName());
                if (attributeValue.getDateValue() != null) {
                    datePicker.setValue(attributeValue.getDateValue());
                }
                datePicker.setWidthFull();
                datePicker.addValueChangeListener(event -> {
                    if (event.getHasValue().isEmpty() && attributeValue.getAttribute().isRequired()) {
                        LOGGER.showError("Attribute '" + attributeValue.getAttribute().getName() + "' can not be empty.");
                    } else {
                        attributeValue.setDateValue(event.getValue());
                        attributeValueService.save(attributeValue);
                        LOGGER.showInfo("Saved '" + attributeValue.getAttribute().getName() + "'!");
                    }
                });
                add(datePicker);
                break;
        }
    }
}