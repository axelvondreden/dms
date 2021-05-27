package com.dude.dms.backend.service

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.repositories.AttributeRepository
import com.dude.dms.brain.DmsLogger.Companion.getLogger
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.t
import org.springframework.stereotype.Service

@Service
class AttributeService(
    private val attributeRepository: AttributeRepository,
    private val tagService: TagService,
    private val attributeValueService: AttributeValueService,
    private val attributeFilterService: AttributeFilterService,
    eventManager: EventManager
) : EventService<Attribute>(attributeRepository, eventManager) {

    override fun create(entity: Attribute): Attribute {
        val current = findByName(entity.name)
        return if (current != null) {
            LOGGER.error(t("attribute.error.duplicate", entity.name))
            current
        } else super.create(entity)
    }

    override fun delete(entity: Attribute) {
        tagService.findByAttribute(entity).forEach {
            it.attributes = it.attributes.minus(entity)
            tagService.save(it)
        }
        attributeValueService.findByAttribute(entity).forEach(attributeValueService::delete)
        entity.attributeFilter?.let(attributeFilterService::delete)
        load(entity.id)?.let { super.delete(it) }
    }

    fun findByName(name: String) = attributeRepository.findByName(name)

    companion object {
        private val LOGGER = getLogger(AttributeService::class.java)
    }
}
