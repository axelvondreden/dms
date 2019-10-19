package com.dude.dms.ui.views;

import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.PlainTextRuleService;
import com.dude.dms.backend.service.RegexRuleService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.components.dialogs.crud.PlainTextRuleDialog;
import com.dude.dms.ui.components.dialogs.crud.RegexRuleDialog;
import com.dude.dms.ui.components.rules.RuleCard;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = Const.PAGE_RULES, layout = MainView.class)
@PageTitle("Rules")
public class RulesView extends VerticalLayout {

    private final Details plainTextDetails;
    private final Details regexDetails;

    private final PlainTextRuleService plainTextRuleService;
    private final RegexRuleService regexRuleService;
    private final TagService tagService;

    @Autowired
    public RulesView(PlainTextRuleService plainTextRuleService, RegexRuleService regexRuleService, TagService tagService) {
        this.plainTextRuleService = plainTextRuleService;
        this.regexRuleService = regexRuleService;
        this.tagService = tagService;

        plainTextDetails = new Details();
        regexDetails = new Details();

        add(plainTextDetails, regexDetails);

        fillContent();
    }

    private void fillContent() {
        addPlaintext();

        addRegex();
    }

    private void addPlaintext() {
        Button create = new Button("Create", e -> {
            PlainTextRuleDialog dialog = new PlainTextRuleDialog(tagService, plainTextRuleService);
            dialog.setEventListener(this::fillContent);
            dialog.open();
        });
        VerticalLayout verticalLayout = new VerticalLayout(create);
        verticalLayout.setSizeFull();
        List<PlainTextRule> plainTextRules = plainTextRuleService.getActiveRules();
        for (PlainTextRule rule : plainTextRules) {
            verticalLayout.add(new RuleCard(rule.getText(), tagService.findByPlainTextRule(rule), e -> {
                PlainTextRuleDialog dialog = new PlainTextRuleDialog(rule, tagService, plainTextRuleService);
                dialog.setEventListener(this::fillContent);
                dialog.open();
            }));
        }
        plainTextDetails.setSummaryText("Plaintext");
        plainTextDetails.setContent(verticalLayout);
    }

    private void addRegex() {
        Button create = new Button("Create", e -> {
            RegexRuleDialog dialog = new RegexRuleDialog(tagService, regexRuleService);
            dialog.setEventListener(this::fillContent);
            dialog.open();
        });
        VerticalLayout verticalLayout = new VerticalLayout(create);
        verticalLayout.setSizeFull();
        List<RegexRule> regexRules = regexRuleService.getActiveRules();
        for (RegexRule rule : regexRules) {
            verticalLayout.add(new RuleCard(rule.getRegex(), tagService.findByRegexRule(rule), e -> {
                RegexRuleDialog dialog = new RegexRuleDialog(rule, tagService, regexRuleService);
                dialog.setEventListener(this::fillContent);
                dialog.open();
            }));
        }
        regexDetails.setSummaryText("Regex");
        regexDetails.setContent(verticalLayout);
    }
}