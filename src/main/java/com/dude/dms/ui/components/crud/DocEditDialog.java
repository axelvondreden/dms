package com.dude.dms.ui.components.crud;

import com.dude.dms.backend.data.base.Doc;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.components.tags.Tagger;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class DocEditDialog extends CrudEditDialog<Doc> {

    private final TextField guidTextField;
    private final DatePicker datePicker;
    private final Tagger tagger;

    private Doc doc;

    private final DocService docService;

    public DocEditDialog(DocService docService, TagService tagService) {
        this.docService = docService;

        guidTextField = new TextField("GUID");
        guidTextField.setWidthFull();
        guidTextField.setReadOnly(true);

        datePicker = new DatePicker("Date");
        datePicker.setWidthFull();

        tagger = new Tagger(tagService);
        tagger.setHeight("25vw");

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

    @Override
    protected void save() {
        doc.setDocumentDate(datePicker.getValue());
        doc.setTags(tagger.getSelectedTags());
        docService.save(doc);
        close();
    }

    @Override
    public void open(Doc item) {
        doc = item;
        guidTextField.setValue(doc.getGuid());
        datePicker.setValue(doc.getDocumentDate());
        tagger.setSelectedTags(doc.getTags());
        open();
    }
}