package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.RegexRuleService
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.dialogs.RegexRuleEditDialog

class RegexRuleEditDialogBuilder(
        private val builderFactory: BuilderFactory,
        private val rule: RegexRule,
        private val regexRuleService: RegexRuleService
): Builder<RegexRuleEditDialog> {

    override fun build() = RegexRuleEditDialog(builderFactory, rule, regexRuleService)
}