package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.RegexRuleService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.standard.RegexField;
import com.dude.dms.ui.components.tags.TagSelector;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class RegexRuleDialog extends EventDialog {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(RegexRuleDialog.class);

    private final RegexField regex;
    private final TagSelector ruleTagSelector;
    private final RegexRuleService regexRuleService;

    private RegexRule regexRule;

    public RegexRuleDialog(BuilderFactory builderFactory, RegexRuleService regexRuleService) {
        this.regexRuleService = regexRuleService;
        regex = new RegexField("Regex");
        regex.setWidthFull();
        ruleTagSelector = builderFactory.tags().selector().build();
        ruleTagSelector.setHeight("80%");
        Button button = new Button("Create", VaadinIcon.PLUS.create(), e -> save());
        button.setWidthFull();
        add(regex, ruleTagSelector, button);
        setWidth("70vw");
        setHeight("70vh");
    }

    public RegexRuleDialog(BuilderFactory builderFactory, RegexRule regexRule, RegexRuleService regexRuleService) {
        this.regexRuleService = regexRuleService;
        this.regexRule = regexRule;
        regex = new RegexField("Regex", regexRule.getRegex());
        regex.setWidthFull();
        ruleTagSelector = builderFactory.tags().selector().forRule(regexRule).build();
        ruleTagSelector.setHeight("80%");
        Button saveButton = new Button("Save", VaadinIcon.DISC.create(), e -> save());
        saveButton.setWidthFull();
        Button deleteButton = new Button("Delete", VaadinIcon.TRASH.create(), e -> delete());
        deleteButton.setWidthFull();
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, deleteButton);
        buttonLayout.setWidthFull();
        add(regex, ruleTagSelector, buttonLayout);
        setWidth("70vw");
        setHeight("70vh");
    }

    private void save() {
        if (regex.isEmpty()) {
            LOGGER.showError("Regex can not be empty!");
            return;
        }
        if (ruleTagSelector.getSelectedTags().isEmpty()) {
            LOGGER.showError("At least on tag must be selected!");
            return;
        }
        if (regexRule == null) {
            regexRuleService.save(new RegexRule(regex.getValue(), ruleTagSelector.getSelectedTags()));
            LOGGER.showInfo("Created new rule!");
        } else {
            regexRule.setRegex(regex.getValue());
            regexRule.setTags(ruleTagSelector.getSelectedTags());
            regexRuleService.save(regexRule);
            LOGGER.showInfo("Edited rule!");
        }
        triggerEvent();
        close();
    }

    private void delete() {
        Dialog dialog = new Dialog(new Label("Are you sure you want to delete the item?"));
        Button button = new Button("Delete", VaadinIcon.TRASH.create(), e -> {
            regexRuleService.delete(regexRule);
            triggerEvent();
            close();
        });
        button.addThemeVariants(ButtonVariant.LUMO_ERROR);
        dialog.add(button);
        dialog.open();
    }
}