package com.dude.dms.backend.service

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.data.docs.Word
import com.dude.dms.backend.repositories.WordRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class WordService(
        private val wordRepository: WordRepository,
        eventManager: EventManager
) : EventService<Word>(wordRepository, eventManager) {

    fun findByLine(line: Line) = wordRepository.findByLine(line)

    fun findByDoc(doc: Doc) = wordRepository.findByLine_Doc(doc)

    fun countByTextAndDoc(text: String, doc: Doc) = wordRepository.countByTextAndLine_Doc(text, doc)

    fun countByText(text: String) = wordRepository.countByText(text)

    fun findByTextAndDoc(text: String, doc: Doc) = wordRepository.findByTextAndLine_Doc(text, doc)

    fun findByText(text: String) = wordRepository.findByText(text)
}