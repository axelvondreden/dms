package com.dude.dms.backend.service

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.filter.AttributeFilter
import com.dude.dms.backend.repositories.AttributeFilterRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class AttributeFilterService(private val attributeFilterRepository: AttributeFilterRepository, eventManager: EventManager) : EventService<AttributeFilter>(attributeFilterRepository, eventManager) {

    override fun create(entity: AttributeFilter) = attributeFilterRepository.findByAttribute(entity.attribute) ?: super.create(entity)

    fun findByAttribute(attribute: Attribute) = attributeFilterRepository.findByAttribute(attribute)
}