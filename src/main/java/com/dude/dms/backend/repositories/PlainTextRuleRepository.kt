package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.rules.PlainTextRule
import org.springframework.data.jpa.repository.JpaRepository

interface PlainTextRuleRepository : JpaRepository<PlainTextRule, Long>