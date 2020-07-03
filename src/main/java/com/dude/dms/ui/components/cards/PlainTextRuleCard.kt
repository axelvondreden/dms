package com.dude.dms.ui.components.cards

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.PlainTextRuleService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
import com.dude.dms.brain.parsing.PlainTextRuleValidator
import com.dude.dms.brain.t
import com.dude.dms.ui.components.tags.TagLayout
import com.dude.dms.ui.plaintextRuleEditDialog
import com.dude.dms.ui.ruleRunnerDialog
import com.dude.dms.ui.tagLayout
import com.github.appreciated.card.RippleClickableCard
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.label
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent

class PlainTextRuleCard(
        docService: DocService,
        tagService: TagService,
        plainTextRuleService: PlainTextRuleService,
        rule: PlainTextRule,
        plainTextRuleValidator: PlainTextRuleValidator,
        eventManager: EventManager
) : RippleClickableCard() {

    private lateinit var label: Label
    private lateinit var tagContainer: TagLayout

    init {
        eventManager.register(this, Tag::class, EventType.UPDATE, EventType.DELETE) { fill(rule) }
        setWidthFull()
        onLeftClick { plaintextRuleEditDialog(plainTextRuleService, rule).open() }

        horizontalLayout(isPadding = true) {
            setWidthFull()
            minHeight = "10vh"
            alignItems = FlexComponent.Alignment.CENTER

            button(t("run"), VaadinIcon.PLAY.create()) {
                onLeftClick {
                    this@horizontalLayout.ruleRunnerDialog(docService, plainTextRuleValidator.runRuleForAll(rule)).open()
                }
                addThemeVariants(ButtonVariant.LUMO_SUCCESS)
            }
            label = label(rule.text)
            tagContainer = tagLayout(tagService, rule.tags.toMutableSet())
        }
    }

    fun fill(rule: PlainTextRule) {
        tagContainer.tags = rule.tags.toMutableSet()
        label.text = rule.text
    }
}