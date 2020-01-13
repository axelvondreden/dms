package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.brain.DmsLogger
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.RegexRuleService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.standard.RegexField
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class RegexRuleEditDialog(
        builderFactory: BuilderFactory,
        private val regexRule: RegexRule,
        private val regexRuleService: RegexRuleService
) : EventDialog<RegexRule>() {

    private val regex = RegexField("Regex", regexRule.regex).apply { setWidthFull() }

    private val ruleTagSelector = builderFactory.tags().selector().forRule(regexRule).build().apply { height = "80%" }

    init {
        width = "70vw"
        height = "70vh"

        val saveButton = Button("Save", VaadinIcon.DISC.create()) { save() }.apply { setWidthFull() }
        val deleteButton = Button("Delete", VaadinIcon.TRASH.create()) { delete() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_ERROR)
        }
        add(regex, ruleTagSelector, HorizontalLayout(saveButton, deleteButton).apply { setWidthFull() })
    }

    private fun delete() {
        val dialog = Dialog(Label("Are you sure you want to delete the item?"))
        val button = Button("Delete", VaadinIcon.TRASH.create()) {
            regexRuleService.delete(regexRule)
            triggerDeleteEvent(regexRule)
            close()
        }.apply { addThemeVariants(ButtonVariant.LUMO_ERROR) }
        dialog.add(button)
        dialog.open()
    }

    private fun save() {
        if (regex.isEmpty) {
            LOGGER.showError("Regex can not be empty!")
            return
        }
        if (ruleTagSelector.selectedTags.isEmpty()) {
            LOGGER.showError("At least on tag must be selected!")
            return
        }
        regexRule.regex = regex.value!!
        regexRule.tags = ruleTagSelector.selectedTags
        regexRuleService.save(regexRule)
        LOGGER.showInfo("Edited rule!")
        triggerEditEvent(regexRule)
        close()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(RegexRuleEditDialog::class.java)
    }
}