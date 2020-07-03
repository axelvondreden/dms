package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.service.PlainTextRuleService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.t
import com.dude.dms.ui.components.tags.TagSelector
import com.dude.dms.ui.tagSelector
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.TextField

class PlainTextRuleCreateDialog(
        private val plainTextRuleService: PlainTextRuleService,
        tagService: TagService
) : DmsDialog("", 70, 70) {

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
        ruleTagSelector = tagSelector(tagService) { height = "80%" }
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