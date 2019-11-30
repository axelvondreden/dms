package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.service.PlainTextRuleService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.tags.TagSelector;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class PlainTextRuleCreateDialog extends EventDialog<PlainTextRule> {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(PlainTextRuleCreateDialog.class);

    private final TextField plainText;
    private final TagSelector ruleTagSelector;
    private final Checkbox caseSensitive;

    private final PlainTextRuleService plainTextRuleService;

    public PlainTextRuleCreateDialog(BuilderFactory builderFactory, PlainTextRuleService plainTextRuleService) {
        this.plainTextRuleService = plainTextRuleService;
        plainText = new TextField("Text");
        plainText.setWidthFull();
        ruleTagSelector = builderFactory.tags().selector().build();
        ruleTagSelector.setHeight("80%");
        caseSensitive = new Checkbox("case sensitive");
        HorizontalLayout hLayout = new HorizontalLayout(plainText, caseSensitive);
        hLayout.setWidthFull();
        hLayout.setAlignItems(FlexComponent.Alignment.END);
        Button button = new Button("Create", VaadinIcon.PLUS.create(), e -> create());
        button.setWidthFull();
        add(hLayout, ruleTagSelector, button);
        setWidth("70vw");
        setHeight("70vh");
    }

    private void create() {
        if (plainText.isEmpty()) {
            LOGGER.showError("Text can not be empty!");
            return;
        }
        if (ruleTagSelector.getSelectedTags().isEmpty()) {
            LOGGER.showError("At least on tag must be selected!");
            return;
        }
        PlainTextRule plainTextRule = new PlainTextRule(plainText.getValue(), caseSensitive.getValue(), ruleTagSelector.getSelectedTags());
        plainTextRuleService.save(plainTextRule);
        LOGGER.showInfo("Created new rule!");
        triggerCreateEvent(plainTextRule);
        close();
    }
}