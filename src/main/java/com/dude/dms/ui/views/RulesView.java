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
import com.github.appreciated.card.Card;
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

    private final Card plainTextWrapperCard;
    private final Card regexWrapperCard;

    private final PlainTextRuleService plainTextRuleService;
    private final RegexRuleService regexRuleService;
    private final TagService tagService;

    @Autowired
    public RulesView(PlainTextRuleService plainTextRuleService, RegexRuleService regexRuleService, TagService tagService) {
        this.plainTextRuleService = plainTextRuleService;
        this.regexRuleService = regexRuleService;
        this.tagService = tagService;

        plainTextWrapperCard = new Card();
        plainTextWrapperCard.setWidthFull();
        regexWrapperCard = new Card();
        regexWrapperCard.setWidthFull();

        add(plainTextWrapperCard, regexWrapperCard);

        fillContent();
    }

    private void fillContent() {
        addPlaintext();

        addRegex();
    }

    private void addPlaintext() {
        plainTextWrapperCard.removeAll();
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
        Details details = new Details("Text", verticalLayout);
        details.setOpened(true);
        details.getElement().getStyle().set("padding", "5px").set("width", "100%");
        plainTextWrapperCard.add(details);
    }

    private void addRegex() {
        regexWrapperCard.removeAll();
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
        Details details = new Details("Regex", verticalLayout);
        details.setOpened(true);
        details.getElement().getStyle().set("padding", "5px").set("width", "100%");
        regexWrapperCard.add(details);
    }
}