package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.filter.PlainTextRule
import org.springframework.data.jpa.repository.JpaRepository

interface PlainTextRuleRepository : JpaRepository<PlainTextRule, Long> {

    fun findByTags(tag: Tag): Set<PlainTextRule>

    fun countByTags(tag: Tag): Long
}