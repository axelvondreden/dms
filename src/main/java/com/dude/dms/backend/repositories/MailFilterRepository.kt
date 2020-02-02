package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.mails.MailFilter
import org.springframework.data.jpa.repository.JpaRepository

interface MailFilterRepository : JpaRepository<MailFilter, Long> {

    fun findByTags(tag: Tag): Set<MailFilter>

    fun countByTags(tag: Tag): Long
}