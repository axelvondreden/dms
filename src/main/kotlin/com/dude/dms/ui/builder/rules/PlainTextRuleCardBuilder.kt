package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.brain.parsing.PlainTextRuleValidator
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.service.TagService
import com.dude.dms.ui.DeleteEvent
import com.dude.dms.ui.EditEvent
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.cards.PlainTextRuleCard

class PlainTextRuleCardBuilder(
        private val builderFactory: BuilderFactory,
        private val rule: PlainTextRule,
        private val plainTextRuleValidator: PlainTextRuleValidator,
        private val tagService: TagService,
        private var editListener: EditEvent<PlainTextRule>? = null,
        private var deleteListener: DeleteEvent<PlainTextRule>? = null) {

    fun build() = PlainTextRuleCard(builderFactory, rule, plainTextRuleValidator, tagService).also {
        it.addClickListener {
            builderFactory.rules().plainEditDialog(rule).withEditListener(editListener).withDeleteListener(deleteListener).build().open()
        }
    }
}