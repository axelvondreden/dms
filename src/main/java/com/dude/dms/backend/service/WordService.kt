package com.dude.dms.backend.service

import com.dude.dms.backend.data.docs.Word
import com.dude.dms.backend.repositories.WordRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class WordService(
        private val wordRepository: WordRepository,
        eventManager: EventManager
) : RestoreService<Word>(wordRepository, eventManager) {

    fun findIncomplete() = wordRepository.findByDeletedIsNull()
}