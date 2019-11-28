package com.dude.dms.ui.components.tags;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.docs.AttributeValue;
import com.dude.dms.backend.service.AttributeValueService;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.function.Function;

public class AttributeValueField extends HorizontalLayout {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(AttributeValueField.class);

    private final Function<Void, Boolean> validation;

    private final String label;

    private final boolean required;

    public AttributeValueField(AttributeValue attributeValue, AttributeValueService attributeValueService) {
        label = attributeValue.getAttribute().getName();
        required = attributeValue.getAttribute().isRequired();
        switch (attributeValue.getAttribute().getType()) {
            case STRING:
                TextField textField = new TextField(label);
                if (attributeValue.getStringValue() != null) {
                    textField.setValue(attributeValue.getStringValue());
                }
                textField.setWidthFull();
                textField.addValueChangeListener(event -> {
                    if (event.getHasValue().isEmpty() && required) {
                        LOGGER.showError("Attribute '" + label + "' can not be empty.");
                    } else {
                        attributeValue.setStringValue(event.getValue());
                        attributeValueService.save(attributeValue);
                        LOGGER.showInfo("Saved '" + label + "'!");
                    }
                });
                add(textField);
                validation = v -> (!required || !textField.isEmpty());
                break;
            case INT:
                NumberField intField = new NumberField(label);
                intField.setStep(1);
                if (attributeValue.getIntValue() != null) {
                    intField.setValue(Double.valueOf(attributeValue.getIntValue()));
                }
                intField.setWidthFull();
                intField.addValueChangeListener(event -> {
                    if (event.getHasValue().isEmpty() && required) {
                        LOGGER.showError("Attribute '" + label + "' can not be empty.");
                    } else {
                        attributeValue.setIntValue(event.getValue().intValue());
                        attributeValueService.save(attributeValue);
                        LOGGER.showInfo("Saved '" + label + "'!");
                    }
                });
                add(intField);
                validation = v -> (!required || !intField.isEmpty());
                break;
            case FLOAT:
                NumberField floatField = new NumberField(label);
                floatField.setStep(0.01);
                if (attributeValue.getFloatValue() != null) {
                    floatField.setValue(Double.valueOf(attributeValue.getFloatValue()));
                }
                floatField.setWidthFull();
                floatField.addValueChangeListener(event -> {
                    if (event.getHasValue().isEmpty() && required) {
                        LOGGER.showError("Attribute '" + label + "' can not be empty.");
                    } else {
                        attributeValue.setFloatValue(event.getValue().floatValue());
                        attributeValueService.save(attributeValue);
                        LOGGER.showInfo("Saved '" + label + "'!");
                    }
                });
                add(floatField);
                validation = v -> (!required || !floatField.isEmpty());
                break;
            case DATE:
                DatePicker datePicker = new DatePicker(label);
                if (attributeValue.getDateValue() != null) {
                    datePicker.setValue(attributeValue.getDateValue());
                }
                datePicker.setWidthFull();
                datePicker.addValueChangeListener(event -> {
                    if (event.getHasValue().isEmpty() && required) {
                        LOGGER.showError("Attribute '" + label + "' can not be empty.");
                    } else {
                        attributeValue.setDateValue(event.getValue());
                        attributeValueService.save(attributeValue);
                        LOGGER.showInfo("Saved '" + label + "'!");
                    }
                });
                add(datePicker);
                validation = v -> (!required || !datePicker.isEmpty());
                break;
            default:
                throw new NotImplementedException();
        }
    }

    public boolean validate() {
        return validation.apply(null);
    }

    public String getLabel() {
        return label;
    }

    public boolean isRequired() {
        return required;
    }
}