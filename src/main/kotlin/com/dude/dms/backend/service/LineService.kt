package com.dude.dms.backend.service

import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.repositories.LineRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class LineService(
        lineRepository: LineRepository,
        private val wordService: WordService,
        eventManager: EventManager
) : RestoreService<Line>(lineRepository, eventManager) {

    override fun delete(entity: Line) {
        entity.words.forEach(wordService::delete)
        load(entity.id)?.let { super.delete(it) }
    }

    override fun softDelete(entity: Line) {
        entity.words.forEach(wordService::softDelete)
        super.softDelete(entity)
    }

    override fun restore(entity: Line) {
        entity.words.forEach(wordService::restore)
        super.restore(entity)
    }
}