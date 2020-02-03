package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.service.PlainTextRuleService
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.dialogs.PlainTextRuleCreateDialog

class PlainTextRuleCreateDialogBuilder(
        private val builderFactory: BuilderFactory,
        private val plainTextRuleService: PlainTextRuleService
): Builder<PlainTextRuleCreateDialog> {

    override fun build() = PlainTextRuleCreateDialog(builderFactory, plainTextRuleService)
}