package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.service.PlainTextRuleService
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField

class PlainTextRuleCreateDialog(builderFactory: BuilderFactory, private val plainTextRuleService: PlainTextRuleService) : Dialog() {

    private val plainText = TextField("Text").apply { setWidthFull() }

    private val ruleTagSelector = builderFactory.tags().selector().build().apply { height = "80%" }

    private val caseSensitive = Checkbox("Case sensitive")

    init {
        width = "70vw"
        height = "70vh"

        val hLayout = HorizontalLayout(plainText, caseSensitive).apply {
            setWidthFull()
            alignItems = FlexComponent.Alignment.END
        }
        val button = Button(t("create"), VaadinIcon.PLUS.create()) { create() }.apply { setWidthFull() }
        add(hLayout, ruleTagSelector, button)
    }

    private fun create() {
        if (plainText.isEmpty) return
        if (ruleTagSelector.selectedTags.isEmpty()) return
        plainTextRuleService.save(PlainTextRule(plainText.value, caseSensitive.value, ruleTagSelector.selectedTags))
        close()
    }
}