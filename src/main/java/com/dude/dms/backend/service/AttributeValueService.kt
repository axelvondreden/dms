package com.dude.dms.backend.service

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.repositories.AttributeValueRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class AttributeValueService(
        private val attributeValueRepository: AttributeValueRepository,
        eventManager: EventManager
) : EventService<AttributeValue>(attributeValueRepository, eventManager) {

    fun findByDocAndAttribute(doc: Doc, attribute: Attribute) = attributeValueRepository.findByDocAndAttribute(doc, attribute)

    fun findByDoc(doc: Doc) = attributeValueRepository.findByDoc(doc)

    fun findByAttribute(attribute: Attribute) = attributeValueRepository.findByAttribute(attribute)
}