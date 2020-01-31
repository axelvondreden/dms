package com.dude.dms.backend.service

import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.repositories.MailFilterRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class MailFilterService(
        mailFilterRepository: MailFilterRepository,
        eventManager: EventManager
) : EventService<MailFilter>(mailFilterRepository, eventManager)