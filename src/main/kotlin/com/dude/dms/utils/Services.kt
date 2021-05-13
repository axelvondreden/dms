package com.dude.dms.utils

import com.dude.dms.backend.service.*
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.SpringContext
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.changelog.ChangelogService

val attributeFilterService by lazy { SpringContext.getBean(AttributeFilterService::class.java)!! }
val attributeService by lazy { SpringContext.getBean(AttributeService::class.java)!! }
val attributeValueService by lazy { SpringContext.getBean(AttributeValueService::class.java)!! }
val changelogService by lazy { SpringContext.getBean(ChangelogService::class.java)!! }
val docService by lazy { SpringContext.getBean(DocService::class.java)!! }
val lineService by lazy { SpringContext.getBean(LineService::class.java)!! }
val logEntryService by lazy { SpringContext.getBean(LogEntryService::class.java)!! }
val pageService by lazy { SpringContext.getBean(PageService::class.java)!! }
val queryService by lazy { SpringContext.getBean(DocFilterService::class.java)!! }
val tagFilterService by lazy { SpringContext.getBean(TagFilterService::class.java)!! }
val tagService by lazy { SpringContext.getBean(TagService::class.java)!! }
val wordService by lazy { SpringContext.getBean(WordService::class.java)!! }

val docParser by lazy { SpringContext.getBean(DocParser::class.java)!! }
val eventManager by lazy { SpringContext.getBean(EventManager::class.java)!! }
val fileManager by lazy { SpringContext.getBean(FileManager::class.java)!! }
