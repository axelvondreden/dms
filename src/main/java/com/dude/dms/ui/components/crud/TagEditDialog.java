package com.dude.dms.ui.components.crud;

import com.dude.dms.backend.data.tags.Tag;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityEventListener;
import com.dude.dms.ui.components.standard.DmsColorPicker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class TagEditDialog extends CrudEditDialog<Tag> {

    private final TextField name;
    private final DmsColorPicker colorPicker;

    private Tag tag;

    private final TagService tagService;

    public TagEditDialog(TagService tagService) {
        this.tagService = tagService;

        name = new TextField("Name");
        name.setWidthFull();

        colorPicker = new DmsColorPicker("Color");
        colorPicker.setWidthFull();

        Button createButton = new Button("Save", e -> save());
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
    protected void save() {
        if (name.isEmpty()) {
            name.setErrorMessage("Name can not be empty!");
            return;
        }
        if (colorPicker.isEmpty()) {
            colorPicker.setErrorMessage("Color can not be empty!");
            return;
        }
        tag.setName(name.getValue());
        tag.setColor(colorPicker.getValue());
        tagService.save(tag);
        eventListener.ifPresent(EntityEventListener::onChange);
        close();
    }

    @Override
    public void open(Tag item) {
        tag = item;
        name.setValue(tag.getName());
        colorPicker.setValue(tag.getColor());
        open();
    }
}