package com.dude.dms.backend.service

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.repositories.LineRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class LineService(
        private val lineRepository: LineRepository,
        private val wordService: WordService,
        eventManager: EventManager
) : EventService<Line>(lineRepository, eventManager) {

    override fun delete(entity: Line) {
        wordService.findByLine(entity).forEach(wordService::delete)
        super.delete(entity)
    }

    fun findByDoc(doc: Doc) = lineRepository.findByDoc(doc)

    fun countByDoc(doc: Doc) = lineRepository.countByDoc(doc)
}