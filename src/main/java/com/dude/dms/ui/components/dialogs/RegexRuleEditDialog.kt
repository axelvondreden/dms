package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.RegexRuleService
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.misc.ConfirmDialog
import com.dude.dms.ui.components.standard.RegexField
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class RegexRuleEditDialog(
        builderFactory: BuilderFactory,
        private val regexRule: RegexRule,
        private val regexRuleService: RegexRuleService) : DmsDialog("", "70vw", "70vh") {

    private val regex = RegexField("Regex", regexRule.regex).apply { setWidthFull() }

    private val ruleTagSelector = builderFactory.tags().selector(rRule = regexRule).apply { height = "80%" }

    init {
        val saveButton = Button(t("save"), VaadinIcon.DISC.create()) { save() }.apply { setWidthFull() }
        val deleteButton = Button(t("delete"), VaadinIcon.TRASH.create()) { delete() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_ERROR)
        }
        add(regex, ruleTagSelector, HorizontalLayout(saveButton, deleteButton).apply { setWidthFull() })
    }

    private fun delete() {
        ConfirmDialog(t("delete.sure"), t("delete"), VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR, ComponentEventListener {
            regexRuleService.delete(regexRule)
            close()
        }).open()
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