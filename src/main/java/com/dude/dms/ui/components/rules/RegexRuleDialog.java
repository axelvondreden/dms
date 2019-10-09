package com.dude.dms.ui.components.rules;

import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.RegexRuleService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.components.regex.RegexField;
import com.dude.dms.ui.components.tags.Tagger;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;

public class RegexRuleDialog extends Dialog {

    private final RegexField regex;
    private final Tagger ruleTagger;
    private final RegexRuleService regexRuleService;

    private RegexRule regexRule;


    /**
     * Constructor for creating an empty dialog. Create button will be added
     *
     * @param tagService       tag-service
     * @param regexRuleService regex-rule-service
     */
    public RegexRuleDialog(TagService tagService, RegexRuleService regexRuleService) {
        this.regexRuleService = regexRuleService;
        regex = new RegexField("Regex");
        regex.setWidthFull();
        ruleTagger = new Tagger(tagService);
        ruleTagger.setHeight("80%");
        Button button = new Button("Create", e -> save());
        button.setWidthFull();
        add(regex, ruleTagger, button);
        setWidth("70vw");
        setHeight("70vh");
    }

    /**
     * Constructor for creating a dialog for an existing rule. Save button will be added
     *
     * @param regexRule        rule
     * @param tagService       tag-service
     * @param regexRuleService regex-rule-service
     */
    public RegexRuleDialog(RegexRule regexRule, TagService tagService, RegexRuleService regexRuleService) {
        this.regexRuleService = regexRuleService;
        this.regexRule = regexRule;
        regex = new RegexField("Regex", regexRule.getRegex());
        regex.setWidthFull();
        ruleTagger = new Tagger(tagService);
        ruleTagger.setSelectedTags(tagService.findByRegexRule(regexRule));
        ruleTagger.setHeight("80%");
        Button button = new Button("Save", e -> save());
        button.setWidthFull();
        add(regex, ruleTagger, button);
        setWidth("70vw");
        setHeight("70vh");
    }

    private void save() {
        if (regex.isEmpty()) {
            Notification.show("Regex can not be empty!");
            return;
        }
        if (ruleTagger.getSelectedTags().isEmpty()) {
            Notification.show("At least on tag must be selected!");
            return;
        }
        if (regexRule == null) {
            regexRuleService.save(new RegexRule(regex.getValue(), ruleTagger.getSelectedTags()));
            Notification.show("Created new rule!");
        } else {
            regexRule.setRegex(regex.getValue());
            regexRule.setTags(ruleTagger.getSelectedTags());
            regexRuleService.save(regexRule);
            Notification.show("Edited rule!");
        }
        close();
    }
}