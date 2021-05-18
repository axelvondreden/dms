package com.dude.dms.backend.service

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Page
import com.dude.dms.backend.repositories.PageRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class PageService(
        private val pageRepository: PageRepository,
        private val lineService: LineService,
        eventManager: EventManager
) : RestoreService<Page>(pageRepository, eventManager) {

    override fun delete(entity: Page) {
        entity.lines.forEach(lineService::delete)
        load(entity.id)?.let { super.delete(it) }
    }

    override fun softDelete(entity: Page) {
        entity.lines.forEach(lineService::softDelete)
        super.softDelete(entity)
    }

    override fun restore(entity: Page) {
        entity.lines.forEach(lineService::restore)
        super.restore(entity)
    }

    fun findByDoc(doc: Doc) = pageRepository.findByDocAndDeletedFalse(doc)
}