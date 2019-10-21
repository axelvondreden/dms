package com.dude.dms.ui.components.dialogs.crud;

import com.dude.dms.backend.data.tags.Tag;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.components.standard.DmsColorPicker;
import com.dude.dms.ui.components.standard.DmsColorPickerSimple;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import static com.dude.dms.backend.brain.OptionKey.SIMPLE_TAG_COLORS;

public class TagCreateDialog extends CrudCreateDialog<Tag> {

    private final TextField name;

    private final Component colorPicker;

    private final TagService tagService;

    public TagCreateDialog(TagService tagService) {
        this.tagService = tagService;

        name = new TextField("Name");
        name.setWidthFull();

        if (SIMPLE_TAG_COLORS.getBoolean()) {
            colorPicker = new DmsColorPickerSimple("Color");
        } else {
            colorPicker = new DmsColorPicker("Color");
        }
        ((HasSize) colorPicker).setWidthFull();

        Button createButton = new Button("Create", e -> create());
        createButton.setWidthFull();
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Close", e -> close());
        cancelButton.setWidthFull();
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout hLayout = new HorizontalLayout(name, colorPicker);
        hLayout.setWidthFull();
        HorizontalLayout buttonLayout = new HorizontalLayout(createButton, cancelButton);
        buttonLayout.setWidthFull();
        VerticalLayout vLayout = new VerticalLayout(hLayout, buttonLayout);
        vLayout.setSizeFull();
        vLayout.setPadding(false);
        vLayout.setSpacing(false);

        add(vLayout);
    }

    @Override
    protected void create() {
        if (name.isEmpty()) {
            name.setErrorMessage("Name can not be empty!");
            return;
        }
        if (((HasValue) colorPicker).isEmpty()) {
            name.setErrorMessage("Color can not be empty!");
            return;
        }
        if (tagService.findByName(name.getValue()).isPresent()) {
            name.setErrorMessage("Tag '" + name.getValue() + "' already exists!");
            return;
        }
        tagService.create(new Tag(name.getValue(), (String) ((HasValue) colorPicker).getValue()));
        if (eventListener != null) {
            eventListener.onChange();
        }
        close();
    }
}