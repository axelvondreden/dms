package com.dude.dms.backend.service

import com.dude.dms.backend.data.docs.DocText
import com.dude.dms.backend.repositories.DocTextRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class DocTextService(
        docTextRepository: DocTextRepository,
        eventManager: EventManager
) : RestoreService<DocText>(docTextRepository, eventManager)
