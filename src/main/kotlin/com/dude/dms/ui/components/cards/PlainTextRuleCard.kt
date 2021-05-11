package com.dude.dms.ui.components.cards

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.filter.PlainTextRule
import com.dude.dms.brain.events.EventType
import com.dude.dms.brain.t
import com.dude.dms.utils.*
import com.dude.dms.ui.components.tags.TagLayout
import com.github.appreciated.card.RippleClickableCard
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.label
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent

class PlainTextRuleCard(rule: PlainTextRule) : RippleClickableCard() {

    private lateinit var label: Label
    private lateinit var tagContainer: TagLayout

    init {
        eventManager.register(this, Tag::class, EventType.UPDATE, EventType.DELETE) { fill(rule) }
        setWidthFull()
        addClickListener { plaintextRuleEditDialog(rule).open() }

        horizontalLayout(isPadding = true) {
            setWidthFull()
            alignItems = FlexComponent.Alignment.CENTER

            button(t("run"), VaadinIcon.PLAY.create()) {
                onLeftClick {
                    this@horizontalLayout.ruleRunnerDialog(plainTextRuleValidator.runRuleForAll(rule)).open()
                }
                addThemeVariants(ButtonVariant.LUMO_SUCCESS)
            }
            label = label(rule.text)
            tagContainer = tagLayout(rule.tags.toMutableSet())
        }
    }

    fun fill(rule: PlainTextRule) {
        tagContainer.tags = rule.tags.toMutableSet()
        label.text = rule.text
    }
}