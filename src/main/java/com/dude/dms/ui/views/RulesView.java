package com.dude.dms.ui.views;

import com.dude.dms.backend.service.PlainTextRuleService;
import com.dude.dms.backend.service.RegexRuleService;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.builder.BuilderFactory;
import com.github.appreciated.card.Card;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = Const.PAGE_RULES, layout = MainView.class)
@PageTitle("Rules")
public class RulesView extends FormLayout {

    private final BuilderFactory builderFactory;

    private final PlainTextRuleService plainTextRuleService;
    private final RegexRuleService regexRuleService;

    @Autowired
    public RulesView(BuilderFactory builderFactory, PlainTextRuleService plainTextRuleService, RegexRuleService regexRuleService) {
        this.builderFactory = builderFactory;
        this.plainTextRuleService = plainTextRuleService;
        this.regexRuleService = regexRuleService;

        getElement().getStyle().set("padding", "10px");
        fillContent();
    }

    private void fillContent() {
        removeAll();
        addPlaintext();
        addRegex();
    }

    private void addPlaintext() {
        Button create = new Button("Create", VaadinIcon.PLUS.create(),
                e -> builderFactory.rules().plainDialog().withCreateListener(entity -> fillContent()).build().open());
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        VerticalLayout verticalLayout = new VerticalLayout(create);
        verticalLayout.setSizeFull();
        plainTextRuleService.getActiveRules().stream()
                .map(rule -> builderFactory.rules().plainTextCard(rule)
                        .withCreateListener(entity -> fillContent())
                        .withEditListener(entity -> fillContent())
                        .withDeleteListener(entity -> fillContent())
                        .build())
                .forEach(verticalLayout::add);
        Details details = new Details("Text", verticalLayout);
        details.setOpened(true);
        details.getElement().getStyle().set("padding", "5px").set("width", "100%");
        Card card = new Card(details);
        card.setSizeFull();
        card.getElement().getStyle().set("height", "100%");
        add(card);
    }

    private void addRegex() {
        Button create = new Button("Create", VaadinIcon.PLUS.create(),
                e -> builderFactory.rules().regexDialog().withCreateListener(entity -> fillContent()).build().open());
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        VerticalLayout verticalLayout = new VerticalLayout(create);
        verticalLayout.setSizeFull();
        regexRuleService.getActiveRules().stream()
                .map(rule -> builderFactory.rules().regexCard(rule)
                        .withCreateListener(entity -> fillContent())
                        .withEditListener(entity -> fillContent())
                        .withDeleteListener(entity -> fillContent())
                        .build())
                .forEach(verticalLayout::add);
        Details details = new Details("Regex", verticalLayout);
        details.setOpened(true);
        details.getElement().getStyle().set("padding", "5px").set("width", "100%");
        Card card = new Card(details);
        card.setSizeFull();
        card.getElement().getStyle().set("height", "100%");
        add(card);
    }
}