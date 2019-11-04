package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.standard.DmsDatePicker;
import com.dude.dms.ui.components.tags.TagSelector;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class DocEditDialog extends EventDialog {

    private final Doc doc;

    private final DmsDatePicker datePicker;

    private final TagSelector tagSelector;

    private final DocService docService;

    public DocEditDialog(BuilderFactory builderFactory, Doc doc, DocService docService) {
        this.doc = doc;
        this.docService = docService;

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

        HorizontalLayout hLayout = new HorizontalLayout(guidTextField, datePicker);
        hLayout.setWidthFull();
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setWidthFull();
        VerticalLayout vLayout = new VerticalLayout(hLayout, tagSelector, buttonLayout);
        vLayout.setSizeFull();
        vLayout.setPadding(false);
        vLayout.setSpacing(false);

        add(vLayout);
    }

    private void save() {
        doc.setDocumentDate(datePicker.getValue());
        doc.setTags(tagSelector.getSelectedTags());
        docService.save(doc);
        triggerEvent();
        close();
    }
}