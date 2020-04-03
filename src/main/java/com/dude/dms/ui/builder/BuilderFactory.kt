package com.dude.dms.ui.builder

import com.dude.dms.backend.service.*
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.brain.parsing.PlainTextRuleValidator
import com.dude.dms.brain.parsing.RegexRuleValidator
import com.dude.dms.brain.polling.DocPollingService
import com.dude.dms.updater.UpdateChecker
import org.springframework.stereotype.Component

@Component
class BuilderFactory(
        private val attributeService: AttributeService,
        private val attributeValueService: AttributeValueService,
        private val changelogService: ChangelogService,
        private val docParser: DocParser,
        private val docPollingService: DocPollingService,
        private val docService: DocService,
        private val eventManager: EventManager,
        private val fileManager: FileManager,
        private val mailFilterService: MailFilterService,
        private val mailManager: MailManager,
        private val mailService: MailService,
        private val plainTextRuleService: PlainTextRuleService,
        private val plainTextRuleValidator: PlainTextRuleValidator,
        private val regexRuleService: RegexRuleService,
        private val regexRuleValidator: RegexRuleValidator,
        private val tagService: TagService,
        private val lineService: LineService,
        private val wordService: WordService,
        private val updateChecker: UpdateChecker
) {

    fun attributes() = AttributeBuilderFactory(this, attributeService, attributeValueService, docService, tagService, eventManager)

    fun docs() = DocBuilderFactory(this, docParser, docPollingService, docService, fileManager, lineService, mailService, tagService, wordService)

    fun misc() = MiscBuilderFactory(this, changelogService, updateChecker)

    fun rules() = RuleBuilderFactory(this, tagService, plainTextRuleValidator, regexRuleValidator, plainTextRuleService, regexRuleService, docService, mailFilterService, mailManager, eventManager)

    fun tags() = TagBuilderFactory(this, tagService, docService, mailService, attributeService, plainTextRuleService, regexRuleService, mailFilterService)
}