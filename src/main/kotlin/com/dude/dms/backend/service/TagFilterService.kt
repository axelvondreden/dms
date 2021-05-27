package com.dude.dms.backend.service

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.filter.TagFilter
import com.dude.dms.backend.repositories.TagFilterRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class TagFilterService(private val tagFilterRepository: TagFilterRepository, eventManager: EventManager) : EventService<TagFilter>(tagFilterRepository, eventManager) {

    override fun create(entity: TagFilter) = tagFilterRepository.findByTag(entity.tag) ?: super.create(entity)

    fun findByTag(tag: Tag) = tagFilterRepository.findByTag(tag)
}
