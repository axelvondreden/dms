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
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class PlainTextRuleDialog extends EventDialog<PlainTextRule> {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(PlainTextRuleDialog.class);

    private final TextField plainText;
    private final TagSelector ruleTagSelector;
    private final Checkbox caseSensitive;

    private final PlainTextRuleService plainTextRuleService;

    private PlainTextRule plainTextRule;

    public PlainTextRuleDialog(BuilderFactory builderFactory, PlainTextRuleService plainTextRuleService) {
        this.plainTextRuleService = plainTextRuleService;
        plainText = new TextField("Text", "");
        plainText.setWidthFull();
        ruleTagSelector = builderFactory.tags().selector().build();
        ruleTagSelector.setHeight("80%");
        caseSensitive = new Checkbox("case sensitive");
        HorizontalLayout hLayout = new HorizontalLayout(plainText, caseSensitive);
        hLayout.setWidthFull();
        hLayout.setAlignItems(FlexComponent.Alignment.END);
        Button button = new Button("Create", VaadinIcon.PLUS.create(), e -> save());
        button.setWidthFull();
        add(hLayout, ruleTagSelector, button);
        setWidth("70vw");
        setHeight("70vh");
    }

    public PlainTextRuleDialog(BuilderFactory builderFactory, PlainTextRule plainTextRule, PlainTextRuleService plainTextRuleService) {
        this.plainTextRuleService = plainTextRuleService;
        this.plainTextRule = plainTextRule;
        plainText = new TextField("Text", "");
        plainText.setValue(plainTextRule.getText());
        plainText.setWidthFull();
        ruleTagSelector = builderFactory.tags().selector().forRule(plainTextRule).build();
        ruleTagSelector.setHeight("80%");
        caseSensitive = new Checkbox("case sensitive");
        caseSensitive.setValue(plainTextRule.getCaseSensitive());
        HorizontalLayout hLayout = new HorizontalLayout(plainText, caseSensitive);
        hLayout.setWidthFull();
        hLayout.setAlignItems(FlexComponent.Alignment.END);
        Button saveButton = new Button("Create", VaadinIcon.PLUS.create(), e -> save());
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
        if (plainTextRule == null) {
            plainTextRuleService.save(new PlainTextRule(plainText.getValue(), caseSensitive.getValue(), ruleTagSelector.getSelectedTags()));
            LOGGER.showInfo("Created new rule!");
        } else {
            plainTextRule.setText(plainText.getValue());
            plainTextRule.setTags(ruleTagSelector.getSelectedTags());
            plainTextRuleService.save(plainTextRule);
            LOGGER.showInfo("Edited rule!");
        }
        triggerCreateEvent(plainTextRule);
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