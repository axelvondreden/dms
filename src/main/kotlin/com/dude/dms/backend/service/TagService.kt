package com.dude.dms.backend.service

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.repositories.TagRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class TagService(
        private val tagRepository: TagRepository,
        private val docService: DocService,
        private val tagFilterService: TagFilterService,
        eventManager: EventManager
) : EventService<Tag>(tagRepository, eventManager) {

    override fun create(entity: Tag) = tagRepository.findByName(entity.name) ?: super.create(entity)

    override fun delete(entity: Tag) {
        entity.tagFilter?.let(tagFilterService::delete)
        docService.findByTag(entity).forEach {
            it.tags = it.tags.minus(entity)
            docService.save(it)
        }
        super.delete(entity)
    }

    fun findByName(name: String) = tagRepository.findByName(name)

    fun findByAttribute(attribute: Attribute) = tagRepository.findByAttributes(attribute)
}
