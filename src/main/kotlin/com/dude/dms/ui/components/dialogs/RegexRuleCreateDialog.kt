package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.brain.t
import com.dude.dms.utils.regexField
import com.dude.dms.utils.regexRuleService
import com.dude.dms.utils.tagSelector
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.icon.VaadinIcon

class RegexRuleCreateDialog : DmsDialog("", 70, 70) {

    init {
        val regex = regexField("Regex") { setWidthFull() }
        val ruleTagSelector = tagSelector { height = "80%" }
        button(t("create"), VaadinIcon.PLUS.create()) {
            onLeftClick {
                if (regex.isEmpty) return@onLeftClick
                if (ruleTagSelector.selectedTags.isEmpty()) return@onLeftClick
                val regexRule = RegexRule(regex.value!!, ruleTagSelector.selectedTags.map { it.tag }.toSet())
                regexRuleService.save(regexRule)
                close()
            }
            setWidthFull()
        }
    }
}