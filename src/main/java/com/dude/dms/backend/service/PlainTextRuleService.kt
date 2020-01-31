package com.dude.dms.backend.service

import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.repositories.PlainTextRuleRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class PlainTextRuleService(
        ruleRepository: PlainTextRuleRepository,
        eventManager: EventManager
) : EventService<PlainTextRule>(ruleRepository, eventManager)