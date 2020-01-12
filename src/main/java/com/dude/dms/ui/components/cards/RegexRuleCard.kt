package com.dude.dms.ui.components.cards

import com.dude.dms.backend.brain.parsing.RegexRuleValidator
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.TagService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.tags.TagContainer
import com.github.appreciated.card.RippleClickableCard
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class RegexRuleCard(
        builderFactory: BuilderFactory,
        rule: RegexRule,
        regexRuleValidator: RegexRuleValidator,
        tagService: TagService
) : RippleClickableCard() {

    init {
        setWidthFull()
        val tagContainer = TagContainer(tagService.findByRegexRule(rule)).apply {
            isPadding = true
        }
        val runButton = Button("Run", VaadinIcon.PLAY.create()) { builderFactory.rules().ruleRunnerDialog(regexRuleValidator.runRuleForAll(rule)).build().open() }.apply {
            addThemeVariants(ButtonVariant.LUMO_SUCCESS)
        }
        val wrapper = HorizontalLayout(runButton, Label(rule.regex), tagContainer).apply {
            setWidthFull()
            minHeight = "10vh"
            alignItems = FlexComponent.Alignment.CENTER
            isPadding = true
        }
        add(wrapper)
    }
}