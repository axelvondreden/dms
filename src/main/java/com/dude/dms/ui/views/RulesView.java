package com.dude.dms.ui.views;

import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.PlainTextRuleService;
import com.dude.dms.backend.service.RegexRuleService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.components.rules.PlainTextRuleDialog;
import com.dude.dms.ui.components.rules.RegexRuleDialog;
import com.dude.dms.ui.components.rules.RuleCard;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = Const.PAGE_RULES, layout = MainView.class)
@PageTitle(Const.TITLE_RULES)
public class RulesView extends VerticalLayout {

    private final Accordion accordion;

    private final PlainTextRuleService plainTextRuleService;
    private final RegexRuleService regexRuleService;
    private final TagService tagService;

    @Autowired
    public RulesView(PlainTextRuleService plainTextRuleService, RegexRuleService regexRuleService, TagService tagService) {
        this.plainTextRuleService = plainTextRuleService;
        this.regexRuleService = regexRuleService;
        this.tagService = tagService;

        accordion = new Accordion();
        accordion.setSizeFull();

        addPlaintext();

        addRegex();

        add(accordion);
    }

    private void addPlaintext() {
        Button create = new Button("Create", e -> new PlainTextRuleDialog(tagService, plainTextRuleService).open());
        VerticalLayout verticalLayout = new VerticalLayout(create);
        verticalLayout.setSizeFull();
        List<PlainTextRule> plainTextRules = plainTextRuleService.getActiveRules();
        for (PlainTextRule rule : plainTextRules) {
            verticalLayout.add(new RuleCard<>(rule.getText(), tagService.findByPlainTextRule(rule), e -> new PlainTextRuleDialog(rule, tagService, plainTextRuleService).open()));
        }
        accordion.add("Plaintext", verticalLayout);
    }

    private void addRegex() {
        Button create = new Button("Create", e -> new RegexRuleDialog(tagService, regexRuleService).open());
        VerticalLayout verticalLayout = new VerticalLayout(create);
        verticalLayout.setSizeFull();
        List<RegexRule> regexRules = regexRuleService.getActiveRules();
        for (RegexRule rule : regexRules) {
            verticalLayout.add(new RuleCard<>(rule.getRegex(), tagService.findByRegexRule(rule), e -> new RegexRuleDialog(rule, tagService, regexRuleService).open()));
        }
        accordion.add("Regex", verticalLayout);
    }
}