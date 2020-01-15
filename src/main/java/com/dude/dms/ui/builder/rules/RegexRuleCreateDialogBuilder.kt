package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.brain.CreateEvent
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.RegexRuleService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.dialogs.RegexRuleCreateDialog

class RegexRuleCreateDialogBuilder(
        private val builderFactory: BuilderFactory,
        private val regexRuleService: RegexRuleService,
        private var createEvent: CreateEvent<RegexRule>? = null) {

    fun build() = RegexRuleCreateDialog(builderFactory, regexRuleService).also { it.createEvent = createEvent }
}