package com.dude.dms.ui.components.rules;

import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.service.PlainTextRuleService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.views.HasNotifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextField;

public class PlainTextRuleDialog extends Dialog implements HasNotifications {

    private final TextField plainText;
    private final RuleTagger<PlainTextRule> ruleTagger;
    private final PlainTextRuleService plainTextRuleService;

    private PlainTextRule plainTextRule;


    /**
     * Constructor for creating an empty dialog. Create button will be added
     * @param tagService tag-service
     * @param plainTextRuleService plaintext-rule-service
     */
    public PlainTextRuleDialog(TagService tagService, PlainTextRuleService plainTextRuleService) {
        this.plainTextRuleService = plainTextRuleService;
        plainText = new TextField("Text", "");
        plainText.setWidthFull();
        ruleTagger = new RuleTagger<>(tagService);
        ruleTagger.setHeight("80%");
        Button button = new Button("Create", e -> save());
        button.setWidthFull();
        add(plainText, ruleTagger, button);
        setWidth("70vw");
        setHeight("70vh");
    }

    /**
     * Constructor for creating a dialog for an existing rule. Save button will be added
     * @param plainTextRule rule
     * @param tagService tag-service
     * @param plainTextRuleService plaintext-rule-service
     */
    public PlainTextRuleDialog(PlainTextRule plainTextRule, TagService tagService, PlainTextRuleService plainTextRuleService) {
        this.plainTextRuleService = plainTextRuleService;
        this.plainTextRule = plainTextRule;
        plainText = new TextField("Text", plainTextRule.getText(), "");
        plainText.setWidthFull();
        ruleTagger = new RuleTagger<>(tagService.findByPlainTextRule(plainTextRule), tagService);
        ruleTagger.setHeight("80%");
        Button button = new Button("Save", e -> save());
        button.setWidthFull();
        add(plainText, ruleTagger, button);
        setWidth("70vw");
        setHeight("70vh");
    }

    private void save() {
        if (plainText.isEmpty()) {
            showNotification("Text can not be empty!", true);
            return;
        }
        if (!ruleTagger.validate()) {
            showNotification("At least on tag must be selected!", true);
            return;
        }
        if (plainTextRule == null) {
            plainTextRuleService.save(new PlainTextRule(plainText.getValue(), ruleTagger.getRuleTags()));
            showNotification("Created new rule!");
        } else {
            plainTextRule.setText(plainText.getValue());
            plainTextRule.setTags(ruleTagger.getRuleTags());
            plainTextRuleService.save(plainTextRule);
            showNotification("Edited rule!");
        }
        close();
    }
}