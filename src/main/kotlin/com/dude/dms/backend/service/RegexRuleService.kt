package com.dude.dms.backend.service

import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.repositories.RegexRuleRepository
import org.springframework.stereotype.Service

@Service
class RegexRuleService(ruleRepository: RegexRuleRepository) : CrudService<RegexRule>(ruleRepository), RuleService<RegexRule> {
    override val activeRules = ruleRepository.findByActive(true)
}