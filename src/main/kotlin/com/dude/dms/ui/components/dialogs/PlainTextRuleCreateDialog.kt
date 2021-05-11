package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.filter.PlainTextRule
import com.dude.dms.brain.t
import com.dude.dms.utils.plainTextRuleService
import com.dude.dms.utils.tagSelector
import com.dude.dms.ui.components.tags.TagSelector
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.TextField

class PlainTextRuleCreateDialog : DmsDialog("", 70, 70) {

    private lateinit var plainText: TextField

    private var ruleTagSelector: TagSelector

    private lateinit var caseSensitive: Checkbox

    init {
        horizontalLayout {
            setWidthFull()
            alignItems = FlexComponent.Alignment.END

            plainText = textField("Text") { setWidthFull() }
            caseSensitive = checkBox("Case sensitive")
        }
        ruleTagSelector = tagSelector { height = "80%" }
        button(t("create"), VaadinIcon.PLUS.create()) {
            onLeftClick { create() }
            setWidthFull()
        }
    }

    private fun create() {
        if (plainText.isEmpty) return
        if (ruleTagSelector.selectedTags.isEmpty()) return
        plainTextRuleService.save(PlainTextRule(plainText.value, caseSensitive.value, ruleTagSelector.selectedTags.map { it.tag }.toSet()))
        close()
    }
}