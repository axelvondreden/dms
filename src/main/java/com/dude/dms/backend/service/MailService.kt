package com.dude.dms.backend.service

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.mails.Mail
import com.dude.dms.backend.repositories.MailRepository
import org.springframework.stereotype.Service

@Service
class MailService(private val mailRepository: MailRepository) : CrudService<Mail>(mailRepository) {

    fun findByDoc(doc: Doc) = mailRepository.findByDocs(doc)

    fun countByDoc(doc: Doc) = mailRepository.countByDocs(doc)
}