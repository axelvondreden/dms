package com.dude.dms.ui.components.rules;

import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.RegexRuleService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.components.regex.RegexField;
import com.dude.dms.ui.views.HasNotifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

public class RegexRuleDialog extends Dialog implements HasNotifications {

    private final RegexField regex;
    private final RuleTagger<RegexRule> ruleTagger;
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
        ruleTagger = new RuleTagger<>(tagService);
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
        ruleTagger = new RuleTagger<>(tagService.findByRegexRule(regexRule), tagService);
        ruleTagger.setHeight("80%");
        Button button = new Button("Save", e -> save());
        button.setWidthFull();
        add(regex, ruleTagger, button);
        setWidth("70vw");
        setHeight("70vh");
    }

    private void save() {
        if (regex.isEmpty()) {
            showNotification("Regex can not be empty!", true);
            return;
        }
        if (!ruleTagger.validate()) {
            showNotification("At least on tag must be selected!", true);
            return;
        }
        if (regexRule == null) {
            regexRuleService.save(new RegexRule(regex.getValue(), ruleTagger.getRuleTags()));
            showNotification("Created new rule!");
        } else {
            regexRule.setRegex(regex.getValue());
            regexRule.setTags(ruleTagger.getRuleTags());
            regexRuleService.save(regexRule);
            showNotification("Edited rule!");
        }
        close();
    }
}