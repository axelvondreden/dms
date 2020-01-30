package com.dude.dms.ui.builder

import com.dude.dms.brain.FileManager
import com.dude.dms.brain.parsing.PlainTextRuleValidator
import com.dude.dms.brain.parsing.RegexRuleValidator
import com.dude.dms.brain.polling.DocPollingService
import com.dude.dms.backend.service.*
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.ui.builder.attributes.AttributeBuilderFactory
import com.dude.dms.ui.builder.docs.DocBuilderFactory
import com.dude.dms.ui.builder.misc.MiscBuilderFactory
import com.dude.dms.ui.builder.rules.RuleBuilderFactory
import com.dude.dms.ui.builder.tags.TagBuilderFactory
import com.dude.dms.updater.UpdateChecker
import org.springframework.stereotype.Component

@Component
class BuilderFactory(
        private val attributeService: AttributeService,
        private val attributeValueService: AttributeValueService,
        private val changelogService: ChangelogService,
        private val docService: DocService,
        private val fileManager: FileManager,
        private val mailFilterService: MailFilterService,
        private val mailManager: MailManager,
        private val mailService: MailService,
        private val plainTextRuleService: PlainTextRuleService,
        private val plainTextRuleValidator: PlainTextRuleValidator,
        private val pollingService: DocPollingService,
        private val regexRuleService: RegexRuleService,
        private val regexRuleValidator: RegexRuleValidator,
        private val tagService: TagService,
        private val textBlockService: TextBlockService,
        private val updateChecker: UpdateChecker
) {

    fun attributes() = AttributeBuilderFactory(this, attributeService, attributeValueService)

    fun docs() = DocBuilderFactory(this, docService, mailService, textBlockService, pollingService, fileManager)

    fun misc() = MiscBuilderFactory(this, changelogService, updateChecker)

    fun rules() = RuleBuilderFactory(this, tagService, plainTextRuleValidator, regexRuleValidator, plainTextRuleService, regexRuleService, docService, mailFilterService, mailManager)

    fun tags() = TagBuilderFactory(this, tagService, docService)
}