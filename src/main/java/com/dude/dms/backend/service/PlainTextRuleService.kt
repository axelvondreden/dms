package com.dude.dms.backend.service

import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.repositories.PlainTextRuleRepository
import org.springframework.stereotype.Service

@Service
class PlainTextRuleService(ruleRepository: PlainTextRuleRepository) : CrudService<PlainTextRule>(ruleRepository)