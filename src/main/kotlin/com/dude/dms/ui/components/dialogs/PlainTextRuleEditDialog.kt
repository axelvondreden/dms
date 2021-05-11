package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.filter.PlainTextRule
import com.dude.dms.brain.t
import com.dude.dms.utils.plainTextRuleService
import com.dude.dms.utils.tagSelector
import com.dude.dms.ui.components.tags.TagSelector
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.TextField

class PlainTextRuleEditDialog(private val plainTextRule: PlainTextRule) : DmsDialog("", 70, 70) {

    private lateinit var plainText: TextField

    private lateinit var ruleTagSelector: TagSelector

    private lateinit var caseSensitive: Checkbox

    init {
        horizontalLayout {
            setWidthFull()
            alignItems = FlexComponent.Alignment.END

            textField("Text") {
                setWidthFull()
                value = plainTextRule.text
            }
            checkBox("Case sensitive") {
                value = plainTextRule.caseSensitive
            }
        }
        tagSelector {
            height = "80%"
            selectedTags = plainTextRule.tags.map { TagContainer(it) }.toSet()
        }
        horizontalLayout {
            setWidthFull()

            button(t("save"), VaadinIcon.PLUS.create()) {
                onLeftClick { save() }
                setWidthFull()
            }
            button(t("delete"), VaadinIcon.TRASH.create()) {
                onLeftClick { delete() }
                setWidthFull()
                addThemeVariants(ButtonVariant.LUMO_ERROR)
            }
        }
    }

    private fun delete() {
        DmsConfirmDialog(t("delete.sure"), t("delete"), VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR) {
            plainTextRuleService.delete(plainTextRule)
            close()
        }.open()
    }

    private fun save() {
        if (plainText.isEmpty) return
        if (ruleTagSelector.selectedTags.isEmpty()) return
        plainTextRule.text = plainText.value
        plainTextRule.tags = ruleTagSelector.selectedTags.map { it.tag }.toSet()
        plainTextRule.caseSensitive = caseSensitive.value
        plainTextRuleService.save(plainTextRule)
        close()
    }
}