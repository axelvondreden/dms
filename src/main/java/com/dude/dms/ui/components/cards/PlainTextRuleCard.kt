package com.dude.dms.ui.components.cards

import com.dude.dms.brain.parsing.PlainTextRuleValidator
import com.dude.dms.backend.data.rules.PlainTextRule
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

class PlainTextRuleCard(
        builderFactory: BuilderFactory,
        rule: PlainTextRule,
        plainTextRuleValidator: PlainTextRuleValidator,
        tagService: TagService
) : RippleClickableCard() {

    init {
        setWidthFull()
        val tagContainer = TagContainer(tagService.findByPlainTextRule(rule)).apply {
            isPadding = true
        }
        val runButton = Button("Run", VaadinIcon.PLAY.create()) { builderFactory.rules().ruleRunnerDialog(plainTextRuleValidator.runRuleForAll(rule)).build().open() }.apply {
            addThemeVariants(ButtonVariant.LUMO_SUCCESS)
        }
        val wrapper = HorizontalLayout(runButton, Label(rule.text), tagContainer).apply {
            setWidthFull()
            minHeight = "10vh"
            alignItems = FlexComponent.Alignment.CENTER
            isPadding = true
        }
        add(wrapper)
    }
}