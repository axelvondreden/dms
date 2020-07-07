package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.RegexRuleService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.t
import com.dude.dms.extensions.regexField
import com.dude.dms.extensions.tagSelector
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.icon.VaadinIcon

class RegexRuleCreateDialog(
        private val regexRuleService: RegexRuleService,
        tagService: TagService
) : DmsDialog("", 70, 70) {

    init {
        val regex = regexField("Regex") { setWidthFull() }
        val ruleTagSelector = tagSelector(tagService) { height = "80%" }
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