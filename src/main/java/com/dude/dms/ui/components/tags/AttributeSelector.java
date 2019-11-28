package com.dude.dms.ui.components.tags;

import com.dude.dms.backend.data.docs.Attribute;
import com.dude.dms.backend.service.AttributeService;
import com.dude.dms.ui.components.misc.IconCheckbox;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.HashSet;
import java.util.Set;

public class AttributeSelector extends VerticalLayout {

    private final Set<Attribute> selected;
    private final Set<Attribute> available;

    private final Grid<Attribute> selectedGrid;
    private final Grid<Attribute> availableGrid;

    public AttributeSelector(AttributeService attributeService) {
        selected = new HashSet<>();
        available = new HashSet<>(attributeService.findAll());

        selectedGrid = new Grid<>();
        selectedGrid.setItems(selected);
        selectedGrid.setMinHeight("100%");
        selectedGrid.setWidth("50%");
        selectedGrid.addColumn(attribute -> attribute.getName() + (attribute.isRequired() ? "*" : "")).setHeader("Selected");
        selectedGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        selectedGrid.addItemClickListener(event -> {
            selected.remove(event.getItem());
            available.add(event.getItem());
            refresh();
        });

        availableGrid = new Grid<>();
        availableGrid.setItems(available);
        availableGrid.setMinHeight("100%");
        availableGrid.setWidth("50%");
        availableGrid.addColumn(attribute -> attribute.getName() + (attribute.isRequired() ? "*" : "")).setHeader("Available");
        availableGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        availableGrid.addItemClickListener(event -> {
            available.remove(event.getItem());
            selected.add(event.getItem());
            refresh();
        });

        HorizontalLayout listWrapper = new HorizontalLayout(selectedGrid, availableGrid);
        listWrapper.setSizeFull();

        TextField addField = new TextField("", "", "New Attribute");
        ComboBox<Attribute.Type> addCombo = new ComboBox<>();
        addCombo.setPreventInvalidInput(true);
        addCombo.setAllowCustomValue(false);
        addCombo.setItems(Attribute.Type.values());
        addCombo.setValue(Attribute.Type.STRING);
        IconCheckbox addCheckbox = new IconCheckbox("", "Required", VaadinIcon.LOCK);
        Button addButton = new Button(VaadinIcon.PLUS.create(), e -> {
            if (!addField.isEmpty() && !addCombo.isEmpty()) {
                available.add(attributeService.create(new Attribute(addField.getValue(), addCheckbox.getValue(), addCombo.getValue())));
                refresh();
            }
        });

        HorizontalLayout addWrapper = new HorizontalLayout(addField, addCombo, addCheckbox, addButton);
        addWrapper.setWidthFull();
        addWrapper.setAlignItems(Alignment.CENTER);

        add(listWrapper, addWrapper);
    }

    private void refresh() {
        selectedGrid.getDataProvider().refreshAll();
        availableGrid.getDataProvider().refreshAll();
    }

    public void setSelectedAttributes(Set<Attribute> selected) {
        available.addAll(this.selected);
        this.selected.clear();

        this.selected.addAll(selected);
        available.removeAll(this.selected);
        refresh();
    }

    public Set<Attribute> getSelectedAttributes() {
        return selected;
    }
}