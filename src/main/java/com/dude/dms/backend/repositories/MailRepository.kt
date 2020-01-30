package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.mails.Mail
import org.springframework.data.jpa.repository.JpaRepository

interface MailRepository : JpaRepository<Mail, Long> {

    fun findByDocs(doc: Doc): List<Mail>

    fun countByDocs(doc: Doc): Int
}