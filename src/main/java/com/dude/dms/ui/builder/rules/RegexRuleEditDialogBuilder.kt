package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.brain.DeleteEvent
import com.dude.dms.backend.brain.EditEvent
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.RegexRuleService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.dialogs.RegexRuleEditDialog

class RegexRuleEditDialogBuilder(
        private val builderFactory: BuilderFactory,
        private val rule: RegexRule,
        private val regexRuleService: RegexRuleService,
        private var deleteEvent: DeleteEvent<RegexRule>? = null,
        private var editEvent: EditEvent<RegexRule>? = null) {

    fun build() = RegexRuleEditDialog(builderFactory, rule, regexRuleService).also {
        it.editEvent = editEvent
        it.deleteEvent = deleteEvent
    }
}