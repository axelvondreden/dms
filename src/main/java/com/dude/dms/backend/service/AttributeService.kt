package com.dude.dms.backend.service

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.repositories.AttributeRepository
import com.dude.dms.brain.DmsLogger.Companion.getLogger
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class AttributeService(
        private val attributeRepository: AttributeRepository,
        eventManager: EventManager
) : EventService<Attribute>(attributeRepository, eventManager) {

    override fun create(entity: Attribute): Attribute {
        val current = findByName(entity.name)
        return if (current != null) {
            LOGGER.error("Attribute '${entity.name}' already exists!")
            current
        } else super.create(entity)
    }

    fun findByName(name: String) = attributeRepository.findByName(name)

    fun findByTag(tag: Tag) = attributeRepository.findByTags(tag)

    fun findByAttributeValue(attributeValue: AttributeValue) = attributeRepository.findByAttributeValues(attributeValue)

    companion object {
        private val LOGGER = getLogger(AttributeService::class.java)
    }
}