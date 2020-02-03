package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.service.PlainTextRuleService
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.dialogs.PlainTextRuleEditDialog

class PlainTextRuleEditDialogBuilder(
        private val builderFactory: BuilderFactory,
        private val rule: PlainTextRule,
        private val plainTextRuleService: PlainTextRuleService
): Builder<PlainTextRuleEditDialog> {

    override fun build() = PlainTextRuleEditDialog(builderFactory, rule, plainTextRuleService)
}