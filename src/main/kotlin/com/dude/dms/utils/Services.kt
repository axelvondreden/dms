package com.dude.dms.utils

import com.dude.dms.backend.service.*
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.SpringContext
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.brain.parsing.PlainTextRuleValidator
import com.dude.dms.brain.parsing.RegexRuleValidator
import com.dude.dms.changelog.ChangelogService

val attributeService by lazy { SpringContext.getBean(AttributeService::class.java)!! }
val attributeValueService by lazy { SpringContext.getBean(AttributeValueService::class.java)!! }
val changelogService by lazy { SpringContext.getBean(ChangelogService::class.java)!! }
val conditionService by lazy { SpringContext.getBean(ConditionService::class.java)!! }
val docService by lazy { SpringContext.getBean(DocService::class.java)!! }
val lineService by lazy { SpringContext.getBean(LineService::class.java)!! }
val logEntryService by lazy { SpringContext.getBean(LogEntryService::class.java)!! }
val pageService by lazy { SpringContext.getBean(PageService::class.java)!! }
val plainTextRuleService by lazy { SpringContext.getBean(PlainTextRuleService::class.java)!! }
val queryService by lazy { SpringContext.getBean(QueryService::class.java)!! }
val regexRuleService by lazy { SpringContext.getBean(RegexRuleService::class.java)!! }
val tagService by lazy { SpringContext.getBean(TagService::class.java)!! }
val wordService by lazy { SpringContext.getBean(WordService::class.java)!! }

val docParser by lazy { SpringContext.getBean(DocParser::class.java)!! }
val eventManager by lazy { SpringContext.getBean(EventManager::class.java)!! }
val fileManager by lazy { SpringContext.getBean(FileManager::class.java)!! }

val plainTextRuleValidator by lazy { SpringContext.getBean(PlainTextRuleValidator::class.java)!! }
val regexRuleValidator by lazy { SpringContext.getBean(RegexRuleValidator::class.java)!! }