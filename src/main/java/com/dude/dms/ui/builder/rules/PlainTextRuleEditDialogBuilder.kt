package com.dude.dms.ui.builder.rules

import com.dude.dms.brain.DeleteEvent
import com.dude.dms.brain.EditEvent
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.service.PlainTextRuleService
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.dialogs.PlainTextRuleEditDialog

class PlainTextRuleEditDialogBuilder(
        private val builderFactory: BuilderFactory,
        private val rule: PlainTextRule,
        private val plainTextRuleService: PlainTextRuleService,
        private var editEvent: EditEvent<PlainTextRule>? = null,
        private var deleteEvent: DeleteEvent<PlainTextRule>? = null
): Builder<PlainTextRuleEditDialog> {

    override fun build() = PlainTextRuleEditDialog(builderFactory, rule, plainTextRuleService).also {
        it.editEvent = editEvent
        it.deleteEvent = deleteEvent
    }
}