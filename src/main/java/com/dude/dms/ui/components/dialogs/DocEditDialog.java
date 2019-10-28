package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.components.standard.DmsDatePicker;
import com.dude.dms.ui.components.tags.Tagger;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class DocEditDialog extends EventDialog {

    private final Doc doc;

    private final DmsDatePicker datePicker;
    private final Tagger tagger;


    private final DocService docService;

    public DocEditDialog(Doc doc, DocService docService, TagService tagService) {
        this.doc = doc;
        this.docService = docService;

        TextField guidTextField = new TextField("GUID");
        guidTextField.setWidthFull();
        guidTextField.setReadOnly(true);
        guidTextField.setValue(doc.getGuid());

        datePicker = new DmsDatePicker("Date");
        datePicker.setWidthFull();
        datePicker.setValue(doc.getDocumentDate());

        tagger = new Tagger(tagService);
        tagger.setHeight("25vw");
        tagger.setSelectedTags(doc.getTags());
        tagger.setContainedTags(doc.getRawText());


        Button saveButton = new Button("Save", e -> save());
        saveButton.setWidthFull();
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Close", e -> close());
        cancelButton.setWidthFull();
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout hLayout = new HorizontalLayout(guidTextField, datePicker);
        hLayout.setWidthFull();
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setWidthFull();
        VerticalLayout vLayout = new VerticalLayout(hLayout, tagger, buttonLayout);
        vLayout.setSizeFull();
        vLayout.setPadding(false);
        vLayout.setSpacing(false);

        add(vLayout);
    }

    private void save() {
        doc.setDocumentDate(datePicker.getValue());
        doc.setTags(tagger.getSelectedTags());
        docService.save(doc);
        if (eventListener != null) {
            eventListener.onChange();
        }
        close();
    }
}