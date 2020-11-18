package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.mails.Mail
import com.dude.dms.backend.data.mails.MailFilter
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, Long> {

    fun findByName(name: String): Tag?

    fun findByMailFilters(mailFilter: MailFilter): Set<Tag>

    fun findByMails(mail: Mail): Set<Tag>

    fun findByAttributes(attribute: Attribute): Set<Tag>

    fun countByAttributes(attribute: Attribute): Long
}