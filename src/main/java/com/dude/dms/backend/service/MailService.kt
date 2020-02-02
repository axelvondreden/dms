package com.dude.dms.backend.service

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.mails.Mail
import com.dude.dms.backend.repositories.MailRepository
import com.dude.dms.brain.events.EventManager
import com.dude.dms.ui.dataproviders.MailDataProvider
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class MailService(
        private val mailRepository: MailRepository,
        eventManager: EventManager
) : EventService<Mail>(mailRepository, eventManager) {

    fun findByDoc(doc: Doc) = mailRepository.findByDocs(doc)

    fun countByDoc(doc: Doc) = mailRepository.countByDocs(doc)

    fun findByFilter(filter: MailDataProvider.Filter, pageable: Pageable) = mailRepository.findByFilter(filter.tag, filter.doc, pageable)

    fun countByFilter(filter: MailDataProvider.Filter) = mailRepository.countByFilter(filter.tag, filter.doc)

    fun countByTag(tag: Tag) = mailRepository.countByTags(tag)
}