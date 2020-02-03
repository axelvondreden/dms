package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.parsing.PlainTextRuleValidator
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.cards.PlainTextRuleCard

class PlainTextRuleCardBuilder(
        private val builderFactory: BuilderFactory,
        private val rule: PlainTextRule,
        private val plainTextRuleValidator: PlainTextRuleValidator,
        private val tagService: TagService,
        private val eventManager: EventManager
) : Builder<PlainTextRuleCard> {

    override fun build() = PlainTextRuleCard(builderFactory, rule, plainTextRuleValidator, tagService, eventManager).also {
        it.addClickListener {
            builderFactory.rules().plainEditDialog(rule).build().open()
        }
    }
}