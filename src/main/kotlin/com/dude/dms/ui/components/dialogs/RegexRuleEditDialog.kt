package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.brain.t
import com.dude.dms.utils.*
import com.dude.dms.ui.components.standard.RegexField
import com.dude.dms.ui.components.tags.TagSelector
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon

class RegexRuleEditDialog(private val regexRule: RegexRule) : DmsDialog("", 70, 70) {

    private lateinit var regex: RegexField

    private lateinit var ruleTagSelector: TagSelector

    init {
        regexField("Regex") {
            value = regexRule.regex
            setWidthFull()
        }
        tagSelector {
            selectedTags = regexRule.tags.map { TagContainer(it) }.toSet()
            height = "80%"
        }
        horizontalLayout {
            setWidthFull()

            button(t("save"), VaadinIcon.DISC.create()) {
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
            regexRuleService.delete(regexRule)
            close()
        }.open()
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