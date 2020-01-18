package com.dude.dms.ui.builder

import com.dude.dms.brain.FileManager
import com.dude.dms.brain.parsing.PlainTextRuleValidator
import com.dude.dms.brain.parsing.RegexRuleValidator
import com.dude.dms.brain.polling.DocPollingService
import com.dude.dms.backend.service.*
import com.dude.dms.ui.builder.attributes.AttributeBuilderFactory
import com.dude.dms.ui.builder.docs.DocBuilderFactory
import com.dude.dms.ui.builder.misc.MiscBuilderFactory
import com.dude.dms.ui.builder.rules.RuleBuilderFactory
import com.dude.dms.ui.builder.tags.TagBuilderFactory
import com.dude.dms.updater.UpdateChecker
import org.springframework.stereotype.Component

@Component
class BuilderFactory(
        private val docService: DocService,
        private val tagService: TagService,
        private val plainTextRuleService: PlainTextRuleService,
        private val regexRuleService: RegexRuleService,
        private val changelogService: ChangelogService,
        private val updateChecker: UpdateChecker,
        private val textBlockService: TextBlockService,
        private val plainTextRuleValidator: PlainTextRuleValidator,
        private val regexRuleValidator: RegexRuleValidator,
        private val attributeService: AttributeService,
        private val attributeValueService: AttributeValueService,
        private val pollingService: DocPollingService,
        private val fileManager: FileManager
) {

    fun attributes() = AttributeBuilderFactory(this, attributeService, attributeValueService)

    fun docs() = DocBuilderFactory(this, docService, textBlockService, pollingService, fileManager)

    fun misc() = MiscBuilderFactory(this, changelogService, updateChecker)

    fun rules() = RuleBuilderFactory(this, tagService, plainTextRuleValidator, regexRuleValidator, plainTextRuleService, regexRuleService, docService)

    fun tags() = TagBuilderFactory(this, tagService, docService)
}