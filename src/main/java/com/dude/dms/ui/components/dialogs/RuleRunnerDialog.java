package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.tags.Tag;
import com.dude.dms.backend.service.DocService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.Map;
import java.util.Set;

public class RuleRunnerDialog extends EventDialog {

    private final Map<Doc, Set<Tag>> result;

    private final DocService docService;

    public RuleRunnerDialog(Map<Doc, Set<Tag>> result, DocService docService) {
        this.result = result;
        this.docService = docService;

        setWidth("60vw");
        setHeight("60vh");

        Text text = new Text(result.size() + " documents found");
        Button saveButton = new Button("Save", VaadinIcon.DISC.create(), event -> save());
        saveButton.setWidthFull();
        add(text, saveButton);
    }

    private void save() {
        result.keySet().forEach(doc -> {
            doc.getTags().addAll(result.get(doc));
            docService.save(doc);
            triggerEvent();
        });
        close();
    }
}