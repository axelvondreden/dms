package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.mails.Mail
import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.data.rules.RegexRule
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, Long> {

    fun findByName(name: String): Tag?

    fun findByPlainTextRules(rule: PlainTextRule): Set<Tag>

    fun findByRegexRules(rule: RegexRule): Set<Tag>

    fun findByMailFilters(mailFilter: MailFilter): Set<Tag>

    fun findTop10ByNameContaining(name: String): Set<Tag>

    fun countByNameContaining(name: String): Long

    fun findTop10ByNameContainingIgnoreCase(name: String): Set<Tag>

    fun countByNameContainingIgnoreCase(name: String): Long

    fun findByDocs(doc: Doc): Set<Tag>

    fun findByMails(mail: Mail): Set<Tag>

    fun findByAttributes(attribute: Attribute): Set<Tag>

    fun countByAttributes(attribute: Attribute): Long
}