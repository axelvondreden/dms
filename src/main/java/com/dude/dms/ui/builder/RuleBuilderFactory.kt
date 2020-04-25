package com.dude.dms.ui.builder

import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.*
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.brain.parsing.PlainTextRuleValidator
import com.dude.dms.brain.parsing.RegexRuleValidator
import com.dude.dms.ui.components.cards.MailFilterCard
import com.dude.dms.ui.components.cards.PlainTextRuleCard
import com.dude.dms.ui.components.cards.RegexRuleCard
import com.dude.dms.ui.components.dialogs.*

class RuleBuilderFactory(
        builderFactory: BuilderFactory,
        private val tagService: TagService,
        private val plainTextRuleValidator: PlainTextRuleValidator,
        private val regexRuleValidator: RegexRuleValidator,
        private val plainTextRuleService: PlainTextRuleService,
        private val regexRuleService: RegexRuleService,
        private val docService: DocService,
        private val mailFilterService: MailFilterService,
        private val mailManager: MailManager,
        private val eventManager: EventManager
) : Factory(builderFactory) {

    fun plainTextCard(rule: PlainTextRule)= PlainTextRuleCard(builderFactory, rule, plainTextRuleValidator, eventManager).also {
        it.addClickListener { builderFactory.rules().plainEditDialog(rule).open() }
    }

    fun regexCard(rule: RegexRule) = RegexRuleCard(builderFactory, rule, regexRuleValidator, eventManager).also {
        it.addClickListener { builderFactory.rules().regexEditDialog(rule).open() }
    }

    fun mailCard(mailFilter: MailFilter) = MailFilterCard(mailFilter).also {
        it.addClickListener { builderFactory.rules().mailEditDialog(mailFilter).open() }
    }

    fun plainCreateDialog() = PlainTextRuleCreateDialog(builderFactory, plainTextRuleService)

    fun regexCreateDialog() = RegexRuleCreateDialog(builderFactory, regexRuleService)

    fun mailCreateDialog() = MailFilterCreateDialog(mailFilterService, mailManager)

    fun plainEditDialog(rule: PlainTextRule)= PlainTextRuleEditDialog(builderFactory, rule, plainTextRuleService)

    fun regexEditDialog(rule: RegexRule) = RegexRuleEditDialog(builderFactory, rule, regexRuleService)

    fun mailEditDialog(mailFilter: MailFilter) = MailFilterEditDialog(mailFilter, mailFilterService, mailManager)

    fun ruleRunnerDialog(result: Map<Doc, Set<TagContainer>>) = RuleRunnerDialog(result, docService)
}