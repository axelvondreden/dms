package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.rules.RegexRule
import org.springframework.data.jpa.repository.JpaRepository

interface RegexRuleRepository : JpaRepository<RegexRule, Long> {
    fun findByActive(active: Boolean): List<RegexRule>
}