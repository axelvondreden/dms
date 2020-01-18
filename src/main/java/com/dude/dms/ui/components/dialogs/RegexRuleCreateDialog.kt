package com.dude.dms.ui.components.dialogs

import com.dude.dms.brain.DmsLogger
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.RegexRuleService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.standard.RegexField
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.VaadinIcon

class RegexRuleCreateDialog(builderFactory: BuilderFactory, private val regexRuleService: RegexRuleService) : EventDialog<RegexRule>() {

    private val regex = RegexField("Regex").apply {
        setWidthFull()
    }

    private val ruleTagSelector = builderFactory.tags().selector().build().apply {
        height = "80%"
    }

    init {
        width = "70vw"
        height = "70vh"

        add(regex, ruleTagSelector, Button("Create", VaadinIcon.PLUS.create()) { create() }.apply { setWidthFull() })
    }

    private fun create() {
        if (regex.isEmpty) {
            LOGGER.showError("Regex can not be empty!")
            return
        }
        if (ruleTagSelector.selectedTags.isEmpty()) {
            LOGGER.showError("At least on tag must be selected!")
            return
        }
        val regexRule = RegexRule(regex.value!!, ruleTagSelector.selectedTags)
        regexRuleService.save(regexRule)
        LOGGER.showInfo("Created new rule!")
        triggerCreateEvent(regexRule)
        close()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(RegexRuleCreateDialog::class.java)
    }
}