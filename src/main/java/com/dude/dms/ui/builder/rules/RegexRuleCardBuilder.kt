package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.parsing.RegexRuleValidator
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.cards.RegexRuleCard

class RegexRuleCardBuilder(
        private val builderFactory: BuilderFactory,
        private val rule: RegexRule,
        private val regexRuleValidator: RegexRuleValidator,
        private val tagService: TagService,
        private val eventManager: EventManager
) : Builder<RegexRuleCard> {

    override fun build() = RegexRuleCard(builderFactory, rule, regexRuleValidator, tagService, eventManager).also {
        it.addClickListener { builderFactory.rules().regexEditDialog(rule).build().open() }
    }
}