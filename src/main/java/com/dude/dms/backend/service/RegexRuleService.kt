package com.dude.dms.backend.service

import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.repositories.RegexRuleRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class RegexRuleService(
        ruleRepository: RegexRuleRepository,
        eventManager: EventManager
) : EventService<RegexRule>(ruleRepository, eventManager)