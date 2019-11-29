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

public class AttributeCreateDialog extends EventDialog<Attribute> {

    private final TextField nameTextField;

    private final ComboBox<Attribute.Type> typeComboBox;

    private final IconToggle requiredToggle;

    private final AttributeService attributeService;

    public AttributeCreateDialog(AttributeService attributeService) {
        this.attributeService = attributeService;

        setWidth("35vw");

        nameTextField = new TextField("Name");
        nameTextField.setWidthFull();

        typeComboBox = new ComboBox<>("Type");
        typeComboBox.setWidthFull();
        typeComboBox.setPreventInvalidInput(true);
        typeComboBox.setAllowCustomValue(false);
        typeComboBox.setItems(Attribute.Type.values());
        typeComboBox.setValue(Attribute.Type.STRING);

        requiredToggle = new IconToggle(VaadinIcon.LOCK.create(), VaadinIcon.UNLOCK.create(), "Required");

        HorizontalLayout fieldWrapper = new HorizontalLayout(nameTextField, typeComboBox, requiredToggle);
        fieldWrapper.setWidthFull();
        fieldWrapper.setAlignItems(FlexComponent.Alignment.END);

        Button createButton = new Button("Create", VaadinIcon.PLUS.create(), e -> create());
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

    private void create() {
        if (nameTextField.isEmpty()) {
            nameTextField.setErrorMessage("Name can not be empty!");
            return;
        }

        Attribute attribute = new Attribute(nameTextField.getValue(), requiredToggle.getValue(), typeComboBox.getValue());
        attributeService.create(attribute);
        triggerCreateEvent(attribute);
        close();
    }
}