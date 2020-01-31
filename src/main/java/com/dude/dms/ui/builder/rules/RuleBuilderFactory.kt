package com.dude.dms.ui.builder.rules

import com.dude.dms.brain.parsing.PlainTextRuleValidator
import com.dude.dms.brain.parsing.RegexRuleValidator
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.*
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.builder.Factory

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

    fun plainTextCard(rule: PlainTextRule)= PlainTextRuleCardBuilder(builderFactory, rule, plainTextRuleValidator, tagService, eventManager)

    fun regexCard(rule: RegexRule) = RegexRuleCardBuilder(builderFactory, rule, regexRuleValidator, tagService, eventManager)

    fun mailCard(mailFilter: MailFilter) = MailFilterCardBuilder(builderFactory, mailFilter)

    fun plainCreateDialog() = PlainTextRuleCreateDialogBuilder(builderFactory, plainTextRuleService)

    fun regexCreateDialog() = RegexRuleCreateDialogBuilder(builderFactory, regexRuleService)

    fun mailCreateDialog() = MailFilterCreateDialogBuilder(mailFilterService, mailManager)

    fun plainEditDialog(rule: PlainTextRule)= PlainTextRuleEditDialogBuilder(builderFactory, rule, plainTextRuleService)

    fun regexEditDialog(rule: RegexRule) = RegexRuleEditDialogBuilder(builderFactory, rule, regexRuleService)

    fun mailEditDialog(mailFilter: MailFilter) = MailFilterEditDialogBuilder(mailFilter, mailFilterService, mailManager)

    fun ruleRunnerDialog(result: Map<Doc, Set<Tag>>) = RuleRunnerDialogBuilder(result, docService)
}