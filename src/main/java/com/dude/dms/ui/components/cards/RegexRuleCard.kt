package com.dude.dms.ui.components.cards

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.RegexRuleService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
import com.dude.dms.brain.parsing.RegexRuleValidator
import com.dude.dms.ui.components.tags.TagLayout
import com.dude.dms.extensions.regexRuleEditDialog
import com.dude.dms.extensions.ruleRunnerDialog
import com.dude.dms.extensions.tagLayout
import com.github.appreciated.card.RippleClickableCard
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.label
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent

class RegexRuleCard(
        docService: DocService,
        tagService: TagService,
        regexRuleService: RegexRuleService,
        rule: RegexRule,
        regexRuleValidator: RegexRuleValidator,
        eventManager: EventManager
) : RippleClickableCard() {

    private lateinit var tagContainer: TagLayout

    private lateinit var label: Label

    init {
        eventManager.register(this, Tag::class, EventType.UPDATE, EventType.DELETE) { fill(rule) }
        setWidthFull()
        addClickListener { regexRuleEditDialog(regexRuleService, tagService, rule).open() }

        horizontalLayout(isPadding = true) {
            setWidthFull()
            minHeight = "10vh"
            alignItems = FlexComponent.Alignment.CENTER

            button("Run", VaadinIcon.PLAY.create()) {
                onLeftClick { this@horizontalLayout.ruleRunnerDialog(docService, regexRuleValidator.runRuleForAll(rule)).open() }
                addThemeVariants(ButtonVariant.LUMO_SUCCESS)
            }
            label = label(rule.regex)
            tagContainer = tagLayout(tagService, rule.tags.toMutableSet())
        }
    }

    fun fill(rule: RegexRule) {
        tagContainer.tags = rule.tags.toMutableSet()
        label.text = rule.regex
    }
}