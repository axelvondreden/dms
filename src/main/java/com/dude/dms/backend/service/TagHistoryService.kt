package com.dude.dms.backend.service

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.history.TagHistory
import com.dude.dms.backend.repositories.TagHistoryRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class TagHistoryService(
        private val historyRepository: TagHistoryRepository,
        eventManager: EventManager
) : HistoryCrudService<Tag, TagHistory>(historyRepository, eventManager) {
    override fun getHistory(entity: Tag) = historyRepository.findByTag(entity)
}