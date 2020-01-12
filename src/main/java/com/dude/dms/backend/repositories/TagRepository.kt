package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.data.rules.RegexRule
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, Long> {
    fun findByName(name: String): Tag?
    fun findByPlainTextRules(rule: PlainTextRule): Set<Tag>
    fun findByRegexRules(rule: RegexRule): Set<Tag>
    fun findTop10ByNameContaining(name: String): List<Tag>
    fun countByNameContaining(name: String): Long
    fun findTop10ByNameContainingIgnoreCase(name: String): List<Tag>
    fun countByNameContainingIgnoreCase(name: String): Long
}