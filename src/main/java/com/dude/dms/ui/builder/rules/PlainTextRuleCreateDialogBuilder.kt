package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.service.PlainTextRuleService
import com.dude.dms.backend.brain.CreateEvent
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.dialogs.PlainTextRuleCreateDialog

class PlainTextRuleCreateDialogBuilder(
        private val builderFactory: BuilderFactory,
        private val plainTextRuleService: PlainTextRuleService,
        private var createListener: CreateEvent<PlainTextRule>? = null) {

    fun build() = PlainTextRuleCreateDialog(builderFactory, plainTextRuleService).also { it.createEvent = createListener }
}