package com.dude.dms.ui.components.dialogs

import com.dude.dms.brain.DmsLogger
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.service.PlainTextRuleService
import com.dude.dms.ui.builder.BuilderFactory
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField

class PlainTextRuleCreateDialog(builderFactory: BuilderFactory, private val plainTextRuleService: PlainTextRuleService) : EventDialog<PlainTextRule>() {

    private val plainText = TextField("Text").apply { setWidthFull() }

    private val ruleTagSelector = builderFactory.tags().selector().build().apply { height = "80%" }

    private val caseSensitive = Checkbox("case sensitive")

    init {
        width = "70vw"
        height = "70vh"

        val hLayout = HorizontalLayout(plainText, caseSensitive).apply {
            setWidthFull()
            alignItems = FlexComponent.Alignment.END
        }
        val button = Button("Create", VaadinIcon.PLUS.create()) { create() }.apply { setWidthFull() }
        add(hLayout, ruleTagSelector, button)
    }

    private fun create() {
        if (plainText.isEmpty) {
            LOGGER.showError("Text can not be empty!")
            return
        }
        if (ruleTagSelector.selectedTags.isEmpty()) {
            LOGGER.showError("At least on tag must be selected!")
            return
        }
        val plainTextRule = PlainTextRule(plainText.value, caseSensitive.value, ruleTagSelector.selectedTags)
        plainTextRuleService.save(plainTextRule)
        LOGGER.showInfo("Created new rule!")
        triggerCreateEvent(plainTextRule)
        close()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(PlainTextRuleCreateDialog::class.java)
    }
}