package com.dude.dms.ui.components.dialogs

import com.dude.dms.brain.DmsLogger
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.service.PlainTextRuleService
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

    private val ruleTagSelector = builderFactory.tags().selector().forRule(plainTextRule).build().apply { height = "80%" }

    private val caseSensitive = Checkbox("case sensitive", plainTextRule.caseSensitive)

    init {
        width = "70vw"
        height = "70vh"

        val hLayout = HorizontalLayout(plainText, caseSensitive).apply {
            setWidthFull()
            alignItems = FlexComponent.Alignment.END
        }
        val saveButton = Button("Save", VaadinIcon.PLUS.create()) { save() }.apply { setWidthFull() }
        val deleteButton = Button("Delete", VaadinIcon.TRASH.create()) { delete() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_ERROR)
        }
        add(hLayout, ruleTagSelector, HorizontalLayout(saveButton, deleteButton).apply { setWidthFull() })
    }

    private fun delete() {
        ConfirmDialog("Are you sure you want to delete the item?", "Delete", VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR, ComponentEventListener {
            plainTextRuleService.delete(plainTextRule)
            close()
        }).open()
    }

    private fun save() {
        if (plainText.isEmpty) {
            LOGGER.showError("Text can not be empty!")
            return
        }
        if (ruleTagSelector.selectedTags.isEmpty()) {
            LOGGER.showError("At least on tag must be selected!")
            return
        }
        plainTextRule.text = plainText.value
        plainTextRule.tags = ruleTagSelector.selectedTags
        plainTextRule.caseSensitive = caseSensitive.value
        plainTextRuleService.save(plainTextRule)
        LOGGER.showInfo("Edited rule!")
        close()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(PlainTextRuleEditDialog::class.java)
    }
}