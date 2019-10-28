package com.dude.dms.ui.views;

import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.PlainTextRuleService;
import com.dude.dms.backend.service.RegexRuleService;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.builder.BuilderFactory;
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

    private final BuilderFactory builderFactory;

    private final PlainTextRuleService plainTextRuleService;
    private final RegexRuleService regexRuleService;

    @Autowired
    public RulesView(BuilderFactory builderFactory, PlainTextRuleService plainTextRuleService, RegexRuleService regexRuleService) {
        this.builderFactory = builderFactory;
        this.plainTextRuleService = plainTextRuleService;
        this.regexRuleService = regexRuleService;

        fillContent();
    }

    private void fillContent() {
        removeAll();
        addPlaintext();
        addRegex();
    }

    private void addPlaintext() {
        Button create = new Button("Create", e -> builderFactory.dialogs().plainTextRule().withEventListener(this::fillContent).build().open());
        VerticalLayout verticalLayout = new VerticalLayout(create);
        verticalLayout.setSizeFull();
        List<PlainTextRule> plainTextRules = plainTextRuleService.getActiveRules();
        for (PlainTextRule rule : plainTextRules) {
            verticalLayout.add(builderFactory.cards().plainTextRule(rule).withDialogEventListener(this::fillContent).build());
        }
        Details details = new Details("Text", verticalLayout);
        details.setOpened(true);
        details.getElement().getStyle().set("padding", "5px").set("width", "100%");
        Card card = new Card(details);
        card.setSizeFull();
        add(card);
    }

    private void addRegex() {
        Button create = new Button("Create", e -> builderFactory.dialogs().regexRule().withEventListener(this::fillContent).build().open());
        VerticalLayout verticalLayout = new VerticalLayout(create);
        verticalLayout.setSizeFull();
        List<RegexRule> regexRules = regexRuleService.getActiveRules();
        for (RegexRule rule : regexRules) {
            verticalLayout.add(builderFactory.cards().regexRule(rule).withDialogEventListener(this::fillContent).build());
        }
        Details details = new Details("Regex", verticalLayout);
        details.setOpened(true);
        details.getElement().getStyle().set("padding", "5px").set("width", "100%");
        Card card = new Card(details);
        card.setSizeFull();
        add(card);
    }
}