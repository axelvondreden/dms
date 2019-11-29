package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.data.docs.Attribute;
import com.dude.dms.backend.service.AttributeService;
import com.dude.dms.ui.components.misc.IconToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class AttributeEditDialog extends EventDialog<Attribute> {

    private final Attribute attribute;

    private final TextField nameTextField;

    private final ComboBox<Attribute.Type> typeComboBox;

    private final IconToggle requiredToggle;

    private final AttributeService attributeService;

    public AttributeEditDialog(Attribute attribute, AttributeService attributeService) {
        this.attribute = attribute;
        this.attributeService = attributeService;

        setWidth("35vw");

        nameTextField = new TextField("Name", attribute.getName(), "");
        nameTextField.setWidthFull();

        typeComboBox = new ComboBox<>("Type");
        typeComboBox.setWidthFull();
        typeComboBox.setPreventInvalidInput(true);
        typeComboBox.setAllowCustomValue(false);
        typeComboBox.setItems(Attribute.Type.values());
        typeComboBox.setValue(attribute.getType());

        requiredToggle = new IconToggle(VaadinIcon.LOCK.create(), VaadinIcon.UNLOCK.create(), "Required", attribute.isRequired());

        HorizontalLayout fieldWrapper = new HorizontalLayout(nameTextField, typeComboBox, requiredToggle);
        fieldWrapper.setWidthFull();
        fieldWrapper.setAlignItems(FlexComponent.Alignment.END);

        Button createButton = new Button("Save", VaadinIcon.PLUS.create(), e -> save());
        createButton.setWidthFull();
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Close", VaadinIcon.CLOSE.create(), e -> close());
        cancelButton.setWidthFull();
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout buttonWrapper = new HorizontalLayout(createButton, cancelButton);
        buttonWrapper.setWidthFull();

        VerticalLayout wrapper = new VerticalLayout(fieldWrapper, buttonWrapper);
        wrapper.setSizeFull();
        wrapper.setPadding(false);
        wrapper.setSpacing(false);
        add(wrapper);
    }

    private void save() {
        if (nameTextField.isEmpty()) {
            nameTextField.setErrorMessage("Name can not be empty!");
            return;
        }

        attribute.setName(nameTextField.getValue());
        attribute.setType(typeComboBox.getValue());
        attribute.setRequired(requiredToggle.getValue());
        attributeService.save(attribute);
        triggerEditEvent(attribute);
        close();
    }
}