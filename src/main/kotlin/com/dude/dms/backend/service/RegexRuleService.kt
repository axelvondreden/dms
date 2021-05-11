package com.dude.dms.backend.service

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.filter.RegexRule
import com.dude.dms.backend.repositories.RegexRuleRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class RegexRuleService(
        private val ruleRepository: RegexRuleRepository,
        eventManager: EventManager
) : EventService<RegexRule>(ruleRepository, eventManager) {

    fun findByTag(tag: Tag) = ruleRepository.findByTags(tag)
}