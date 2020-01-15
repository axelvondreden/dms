package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.brain.CreateEvent
import com.dude.dms.backend.brain.DeleteEvent
import com.dude.dms.backend.brain.EditEvent
import com.dude.dms.backend.brain.parsing.PlainTextRuleValidator
import com.dude.dms.backend.brain.parsing.RegexRuleValidator
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.PlainTextRuleService
import com.dude.dms.backend.service.RegexRuleService
import com.dude.dms.backend.service.TagService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.builder.Factory

class RuleBuilderFactory(
        builderFactory: BuilderFactory,
        private val tagService: TagService,
        private val plainTextRuleValidator: PlainTextRuleValidator,
        private val regexRuleValidator: RegexRuleValidator,
        private val plainTextRuleService: PlainTextRuleService,
        private val regexRuleService: RegexRuleService,
        private val docService: DocService) : Factory(builderFactory) {

    fun plainTextCard(
            rule: PlainTextRule,
            editEvent: EditEvent<PlainTextRule>? = null,
            deleteEvent: DeleteEvent<PlainTextRule>? = null
    ) = PlainTextRuleCardBuilder(builderFactory, rule, plainTextRuleValidator, tagService)

    fun regexCard(rule: RegexRule, editEvent: EditEvent<RegexRule>? = null, deleteEvent: DeleteEvent<RegexRule>? = null) = RegexRuleCardBuilder(builderFactory, rule, regexRuleValidator, tagService, editEvent, deleteEvent)

    fun plainCreateDialog(
            createEvent: CreateEvent<PlainTextRule>? = null
    ) = PlainTextRuleCreateDialogBuilder(builderFactory, plainTextRuleService, createEvent)

    fun plainEditDialog(
            rule: PlainTextRule,
            editEvent: EditEvent<PlainTextRule>? = null,
            deleteEvent: DeleteEvent<PlainTextRule>? = null
    ) = PlainTextRuleEditDialogBuilder(builderFactory, rule, plainTextRuleService, editEvent, deleteEvent)

    fun regexCreateDialog(createEvent: CreateEvent<RegexRule>? = null) = RegexRuleCreateDialogBuilder(builderFactory, regexRuleService, createEvent)

    fun regexEditDialog(rule: RegexRule, editEvent: EditEvent<RegexRule>? = null, deleteEvent: DeleteEvent<RegexRule>? = null) =
            RegexRuleEditDialogBuilder(builderFactory, rule, regexRuleService, editEvent, deleteEvent)

    fun ruleRunnerDialog(result: Map<Doc, Set<Tag>>) = RuleRunnerDialogBuilder(result, docService)
}