package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.RegexRuleService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.standard.RegexField;
import com.dude.dms.ui.components.tags.TagSelector;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

public class RegexRuleCreateDialog extends EventDialog<RegexRule> {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(RegexRuleCreateDialog.class);

    private final RegexField regex;
    private final TagSelector ruleTagSelector;
    private final RegexRuleService regexRuleService;

    public RegexRuleCreateDialog(BuilderFactory builderFactory, RegexRuleService regexRuleService) {
        this.regexRuleService = regexRuleService;
        regex = new RegexField("Regex");
        regex.setWidthFull();
        ruleTagSelector = builderFactory.tags().selector().build();
        ruleTagSelector.setHeight("80%");
        Button button = new Button("Create", VaadinIcon.PLUS.create(), e -> create());
        button.setWidthFull();
        add(regex, ruleTagSelector, button);
        setWidth("70vw");
        setHeight("70vh");
    }

    private void create() {
        if (regex.isEmpty()) {
            LOGGER.showError("Regex can not be empty!");
            return;
        }
        if (ruleTagSelector.getSelectedTags().isEmpty()) {
            LOGGER.showError("At least on tag must be selected!");
            return;
        }
        RegexRule regexRule = new RegexRule(regex.getValue(), ruleTagSelector.getSelectedTags());
        regexRuleService.save(regexRule);
        LOGGER.showInfo("Created new rule!");
        triggerCreateEvent(regexRule);
        close();
    }
}