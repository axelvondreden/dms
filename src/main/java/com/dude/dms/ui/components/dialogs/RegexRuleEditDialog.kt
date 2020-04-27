package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.RegexRuleService
import com.dude.dms.brain.t
import com.dude.dms.extensions.resizable
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
) : Dialog() {

    private val regex = RegexField("Regex", regexRule.regex).apply { setWidthFull() }

    private val ruleTagSelector = builderFactory.tags().selector(rRule = regexRule).apply { height = "80%" }

    init {
        resizable()
        width = "70vw"
        height = "70vh"

        val saveButton = Button(t("save"), VaadinIcon.DISC.create()) { save() }.apply { setWidthFull() }
        val deleteButton = Button(t("delete"), VaadinIcon.TRASH.create()) { delete() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_ERROR)
        }
        add(regex, ruleTagSelector, HorizontalLayout(saveButton, deleteButton).apply { setWidthFull() })
    }

    private fun delete() {
        val dialog = Dialog(Label(t("delete.sure")))
        val button = Button(t("delete"), VaadinIcon.TRASH.create()) {
            regexRuleService.delete(regexRule)
            close()
        }.apply { addThemeVariants(ButtonVariant.LUMO_ERROR) }
        dialog.add(button)
        dialog.open()
    }

    private fun save() {
        if (regex.isEmpty) return
        if (ruleTagSelector.selectedTags.isEmpty()) return
        regexRule.regex = regex.value!!
        regexRule.tags = ruleTagSelector.selectedTags.map { it.tag }.toSet()
        regexRuleService.save(regexRule)
        close()
    }
}