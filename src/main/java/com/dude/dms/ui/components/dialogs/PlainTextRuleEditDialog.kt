package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.service.PlainTextRuleService
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.misc.ConfirmDialog
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField

class PlainTextRuleEditDialog(
        builderFactory: BuilderFactory,
        private val plainTextRule: PlainTextRule,
        private val plainTextRuleService: PlainTextRuleService
) : Dialog() {

    private val plainText = TextField("Text", plainTextRule.text, "").apply { setWidthFull() }

    private val ruleTagSelector = builderFactory.tags().selector(pRule = plainTextRule).apply { height = "80%" }

    private val caseSensitive = Checkbox("Case sensitive", plainTextRule.caseSensitive)

    init {
        width = "70vw"
        height = "70vh"

        val hLayout = HorizontalLayout(plainText, caseSensitive).apply {
            setWidthFull()
            alignItems = FlexComponent.Alignment.END
        }
        val saveButton = Button(t("save"), VaadinIcon.PLUS.create()) { save() }.apply { setWidthFull() }
        val deleteButton = Button(t("delete"), VaadinIcon.TRASH.create()) { delete() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_ERROR)
        }
        add(hLayout, ruleTagSelector, HorizontalLayout(saveButton, deleteButton).apply { setWidthFull() })
    }

    private fun delete() {
        ConfirmDialog(t("delete.sure"), t("delete"), VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR, ComponentEventListener {
            plainTextRuleService.delete(plainTextRule)
            close()
        }).open()
    }

    private fun save() {
        if (plainText.isEmpty) return
        if (ruleTagSelector.selectedTags.isEmpty()) return
        plainTextRule.text = plainText.value
        plainTextRule.tags = ruleTagSelector.selectedTags
        plainTextRule.caseSensitive = caseSensitive.value
        plainTextRuleService.save(plainTextRule)
        close()
    }
}