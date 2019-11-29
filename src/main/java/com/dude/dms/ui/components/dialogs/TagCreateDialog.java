package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.standard.DmsColorPicker;
import com.dude.dms.ui.components.standard.DmsColorPickerSimple;
import com.dude.dms.ui.components.tags.AttributeSelector;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import static com.dude.dms.backend.brain.OptionKey.SIMPLE_TAG_COLORS;

public class TagCreateDialog extends EventDialog<Tag> {

    private final TextField name;

    private final Component colorPicker;

    private final TagService tagService;

    private final AttributeSelector attributeSelector;

    public TagCreateDialog(BuilderFactory builderFactory, TagService tagService) {
        this.tagService = tagService;

        setWidth("35vw");

        name = new TextField("Name");
        name.setWidthFull();
        colorPicker = SIMPLE_TAG_COLORS.getBoolean() ? new DmsColorPickerSimple("Color") : new DmsColorPicker("Color");
        ((HasSize) colorPicker).setWidthFull();

        Button createButton = new Button("Create", VaadinIcon.PLUS.create(), e -> create());
        createButton.setWidthFull();
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Close", VaadinIcon.CLOSE.create(), e -> close());
        cancelButton.setWidthFull();
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout fieldWrapper = new HorizontalLayout(name, colorPicker);
        fieldWrapper.setWidthFull();
        HorizontalLayout buttonLayout = new HorizontalLayout(createButton, cancelButton);
        buttonLayout.setWidthFull();

        attributeSelector = builderFactory.attributes().selector().build();
        attributeSelector.setSizeFull();

        Details attributeDetails = new Details("Attributes", attributeSelector);
        attributeDetails.getElement().getStyle().set("width", "100%");

        VerticalLayout vLayout = new VerticalLayout(fieldWrapper, attributeDetails, buttonLayout);
        vLayout.setSizeFull();
        vLayout.setPadding(false);
        vLayout.setSpacing(false);

        add(vLayout);
    }

    private void create() {
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
        Tag tag = new Tag(name.getValue(), (String) ((HasValue) colorPicker).getValue());
        tagService.create(tag);
        tag.setAttributes(attributeSelector.getSelectedAttributes());
        tagService.save(tag);
        triggerCreateEvent(tag);
        close();
    }
}