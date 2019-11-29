package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.standard.DmsDatePicker;
import com.dude.dms.ui.components.tags.AttributeValueContainer;
import com.dude.dms.ui.components.tags.TagSelector;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class DocEditDialog extends EventDialog<Doc> {

    private final Doc doc;

    private final DmsDatePicker datePicker;

    private final TagSelector tagSelector;

    private final AttributeValueContainer attributeValueContainer;

    private final DocService docService;

    public DocEditDialog(BuilderFactory builderFactory, Doc doc, DocService docService) {
        this.doc = doc;
        this.docService = docService;

        setWidth("40vw");

        TextField guidTextField = new TextField("GUID");
        guidTextField.setWidthFull();
        guidTextField.setReadOnly(true);
        guidTextField.setValue(doc.getGuid());

        datePicker = new DmsDatePicker("Date");
        datePicker.setWidthFull();
        datePicker.setValue(doc.getDocumentDate());

        tagSelector = builderFactory.tags().selector().forDoc(doc).build();
        tagSelector.setHeight("25vw");

        Button saveButton = new Button("Save", VaadinIcon.DISC.create(), e -> save());
        saveButton.setWidthFull();
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Close", VaadinIcon.CLOSE.create(), e -> close());
        cancelButton.setWidthFull();
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        attributeValueContainer = builderFactory.attributes().valueContainer(doc).build();
        attributeValueContainer.setSizeFull();
        attributeValueContainer.setMaxHeight("40vh");

        Details tagDetails = new Details("Tags", tagSelector);
        tagDetails.getElement().getStyle().set("width", "100%");

        Details attributeDetails = new Details("Attributes", attributeValueContainer);
        attributeDetails.getElement().getStyle().set("width", "100%");

        HorizontalLayout fieldWrapper = new HorizontalLayout(guidTextField, datePicker);
        fieldWrapper.setWidthFull();
        HorizontalLayout buttonWrapper = new HorizontalLayout(saveButton, cancelButton);
        buttonWrapper.setWidthFull();
        VerticalLayout wrapper = new VerticalLayout(fieldWrapper, tagDetails, attributeDetails, buttonWrapper);
        wrapper.setSizeFull();
        wrapper.setPadding(false);
        wrapper.setSpacing(false);

        add(wrapper);
    }

    private void save() {
        if (attributeValueContainer.validate()) {
            doc.setDocumentDate(datePicker.getValue());
            doc.setTags(tagSelector.getSelectedTags());
            docService.save(doc);
            triggerEditEvent(doc);
            close();
        }
    }
}