package com.dude.dms.ui.components.cards;

import com.dude.dms.backend.brain.parsing.PlainTextRuleValidator;
import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.tags.TagContainer;
import com.github.appreciated.card.RippleClickableCard;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class PlainTextRuleCard extends RippleClickableCard {

    public PlainTextRuleCard(BuilderFactory builderFactory, PlainTextRule rule, PlainTextRuleValidator plainTextRuleValidator, TagService tagService) {
        setWidthFull();

        TagContainer tagContainer = new TagContainer(tagService.findByPlainTextRule(rule));
        tagContainer.setPadding(true);

        Button runButton = new Button("Run", VaadinIcon.PLAY.create(), e -> builderFactory.rules().ruleRunnerDialog(plainTextRuleValidator.runRuleForAll(rule)).build().open());
        runButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        HorizontalLayout wrapper = new HorizontalLayout(runButton, new Label(rule.getText()), tagContainer);
        wrapper.setWidthFull();
        wrapper.setMinHeight("10vh");
        wrapper.setAlignItems(Alignment.CENTER);
        wrapper.setPadding(true);
        add(wrapper);
    }
}