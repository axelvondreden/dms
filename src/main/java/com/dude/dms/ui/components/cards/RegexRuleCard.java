package com.dude.dms.ui.components.cards;

import com.dude.dms.backend.brain.parsing.RegexRuleValidator;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.components.tags.TagContainer;
import com.github.appreciated.card.RippleClickableCard;
import com.github.appreciated.card.label.PrimaryLabel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class RegexRuleCard extends RippleClickableCard {

    public RegexRuleCard(BuilderFactory builderFactory, RegexRule rule, RegexRuleValidator regexRuleValidator, TagService tagService) {
        setWidthFull();

        TagContainer tagContainer = new TagContainer(tagService.findByRegexRule(rule));
        tagContainer.setPadding(true);
        tagContainer.setWidthFull();

        Button runButton = new Button("Run", VaadinIcon.PLAY.create(), e -> builderFactory.dialogs().ruleRunner(regexRuleValidator.runRuleForAll(rule)).build().open());
        runButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        HorizontalLayout wrapper = new HorizontalLayout(runButton, new PrimaryLabel(rule.getRegex()), tagContainer);
        wrapper.setWidthFull();
        wrapper.setMinHeight("15vh");
        wrapper.setAlignItems(Alignment.CENTER);
        add(wrapper);
    }
}