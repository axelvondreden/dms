package com.dude.dms.backend.service

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.repositories.MailFilterRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class MailFilterService(
        private val mailFilterRepository: MailFilterRepository,
        eventManager: EventManager
) : EventService<MailFilter>(mailFilterRepository, eventManager) {

    fun findByTag(tag: Tag) = mailFilterRepository.findByTags(tag)

    fun countByTag(tag: Tag) = mailFilterRepository.countByTags(tag)
}