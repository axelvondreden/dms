package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.service.PlainTextRuleService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.misc.ConfirmDialog;
import com.dude.dms.ui.components.tags.TagSelector;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class PlainTextRuleEditDialog extends EventDialog<PlainTextRule> {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(PlainTextRuleEditDialog.class);

    private final TextField plainText;
    private final TagSelector ruleTagSelector;
    private final Checkbox caseSensitive;

    private final PlainTextRuleService plainTextRuleService;

    private final PlainTextRule plainTextRule;

    public PlainTextRuleEditDialog(BuilderFactory builderFactory, PlainTextRule plainTextRule, PlainTextRuleService plainTextRuleService) {
        this.plainTextRuleService = plainTextRuleService;
        this.plainTextRule = plainTextRule;
        plainText = new TextField("Text", plainTextRule.getText(), "");
        plainText.setWidthFull();
        ruleTagSelector = builderFactory.tags().selector().forRule(plainTextRule).build();
        ruleTagSelector.setHeight("80%");
        caseSensitive = new Checkbox("case sensitive");
        caseSensitive.setValue(plainTextRule.getCaseSensitive());
        HorizontalLayout hLayout = new HorizontalLayout(plainText, caseSensitive);
        hLayout.setWidthFull();
        hLayout.setAlignItems(FlexComponent.Alignment.END);
        Button saveButton = new Button("Save", VaadinIcon.PLUS.create(), e -> save());
        saveButton.setWidthFull();
        Button deleteButton = new Button("Delete", VaadinIcon.TRASH.create(), e -> delete());
        deleteButton.setWidthFull();
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, deleteButton);
        buttonLayout.setWidthFull();
        add(hLayout, ruleTagSelector, buttonLayout);
        setWidth("70vw");
        setHeight("70vh");
    }

    private void save() {
        if (plainText.isEmpty()) {
            LOGGER.showError("Text can not be empty!");
            return;
        }
        if (ruleTagSelector.getSelectedTags().isEmpty()) {
            LOGGER.showError("At least on tag must be selected!");
            return;
        }
        plainTextRule.setText(plainText.getValue());
        plainTextRule.setTags(ruleTagSelector.getSelectedTags());
        plainTextRule.setCaseSensitive(caseSensitive.getValue());
        plainTextRuleService.save(plainTextRule);
        LOGGER.showInfo("Edited rule!");
        triggerEditEvent(plainTextRule);
        close();
    }

    private void delete() {
        new ConfirmDialog("Are you sure you want to delete the item?", "Delete", VaadinIcon.TRASH, e -> {
            plainTextRuleService.delete(plainTextRule);
            triggerDeleteEvent(plainTextRule);
            close();
        }, ButtonVariant.LUMO_ERROR).open();
    }
}