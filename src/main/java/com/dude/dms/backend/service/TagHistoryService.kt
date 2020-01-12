package com.dude.dms.backend.service

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.history.TagHistory
import com.dude.dms.backend.repositories.TagHistoryRepository
import org.springframework.stereotype.Service

@Service
class TagHistoryService(private val historyRepository: TagHistoryRepository) : HistoryCrudService<Tag, TagHistory>(historyRepository) {
    override fun getHistory(entity: Tag) = historyRepository.findByTag(entity)
}