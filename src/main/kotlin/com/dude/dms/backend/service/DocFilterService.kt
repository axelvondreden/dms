package com.dude.dms.backend.service

import com.dude.dms.backend.data.filter.DocFilter
import com.dude.dms.backend.repositories.DocFilterRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class DocFilterService(private val docFilterRepository: DocFilterRepository, eventManager: EventManager) : EventService<DocFilter>(docFilterRepository, eventManager) {

    override fun create(entity: DocFilter) = docFilterRepository.findByName(entity.name) ?: super.create(entity)

    fun findByName(name: String) = docFilterRepository.findByName(name)
}