package com.dude.dms.backend.service

import com.dude.dms.backend.data.Query
import com.dude.dms.backend.repositories.QueryRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class QueryService(private val queryRepository: QueryRepository, eventManager: EventManager) : EventService<Query>(queryRepository, eventManager) {

    override fun create(entity: Query) = queryRepository.findByName(entity.name) ?: super.create(entity)

    fun findByName(name: String) = queryRepository.findByName(name)
}