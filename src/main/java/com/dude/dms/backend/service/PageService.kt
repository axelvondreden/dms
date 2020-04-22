package com.dude.dms.backend.service

import com.dude.dms.backend.data.docs.Page
import com.dude.dms.backend.repositories.PageRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class PageService(
        pageRepository: PageRepository,
        private val lineService: LineService,
        eventManager: EventManager
) : EventService<Page>(pageRepository, eventManager) {

    override fun delete(entity: Page) {
        entity.lines.forEach(lineService::delete)
        load(entity.id)?.let { super.delete(it) }
    }
}