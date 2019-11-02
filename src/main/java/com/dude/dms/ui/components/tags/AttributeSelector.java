package com.dude.dms.ui.components.tags;

import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.data.docs.Attribute;
import com.dude.dms.backend.service.AttributeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;

import java.util.Set;

public class AttributeSelector extends VerticalLayout {

    private final Set<Attribute> selected;
    private final Set<Attribute> available;

    public AttributeSelector(Tag tag, AttributeService attributeService) {
        selected = attributeService.findByTag(tag);
        available = attributeService.findByTagNot(tag);

        ListBox<Attribute> selectedList = new ListBox<>();
        selectedList.setItems(selected);
        selectedList.setHeightFull();
        selectedList.setWidth("50%");
        selectedList.setRenderer(new TextRenderer<>(Attribute::getName));
        selectedList.addValueChangeListener(event -> {
            if (!event.getHasValue().isEmpty()) {
                selected.remove(event.getValue());
                available.add(event.getValue());
            }
        });

        ListBox<Attribute> availableList = new ListBox<>();
        availableList.setItems(available);
        availableList.setHeightFull();
        availableList.setWidth("50%");
        availableList.setRenderer(new TextRenderer<>(Attribute::getName));
        availableList.addValueChangeListener(event -> {
            if (!event.getHasValue().isEmpty()) {
                selected.remove(event.getValue());
                available.add(event.getValue());
            }
        });

        HorizontalLayout listWrapper = new HorizontalLayout(selectedList, availableList);
        listWrapper.setSizeFull();

        TextField addField = new TextField("", "", "New Attribute");
        addField.setWidthFull();
        Checkbox addCheckbox = new Checkbox("Req.");
        Button addButton = new Button(VaadinIcon.PLUS.create(), e -> {
            if (!addField.isEmpty()) {
                attributeService.create(new Attribute(addField.getValue(), addCheckbox.getValue()));
            }
        });

        HorizontalLayout addWrapper = new HorizontalLayout(addField, addCheckbox, addButton);
        addWrapper.setWidthFull();

        add(listWrapper, addWrapper);
    }

    public Set<Attribute> getSelectedAttributes() {
        return selected;
    }
}