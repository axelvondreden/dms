package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.RegexRuleService
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.standard.RegexField
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon

class RegexRuleCreateDialog(builderFactory: BuilderFactory, private val regexRuleService: RegexRuleService) : Dialog() {

    private val regex = RegexField("Regex").apply {
        setWidthFull()
    }

    private val ruleTagSelector = builderFactory.tags().selector().apply {
        height = "80%"
    }

    init {
        width = "70vw"
        height = "70vh"

        add(regex, ruleTagSelector, Button(t("create"), VaadinIcon.PLUS.create()) { create() }.apply { setWidthFull() })
    }

    private fun create() {
        if (regex.isEmpty) return
        if (ruleTagSelector.selectedTags.isEmpty()) return
        val regexRule = RegexRule(regex.value!!, ruleTagSelector.selectedTags.map { it.tag }.toSet())
        regexRuleService.save(regexRule)
        close()
    }
}