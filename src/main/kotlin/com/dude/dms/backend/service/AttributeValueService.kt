package com.dude.dms.backend.service

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.repositories.AttributeValueRepository
import org.springframework.stereotype.Service

@Service
class AttributeValueService(private val attributeValueRepository: AttributeValueRepository) : CrudService<AttributeValue>(attributeValueRepository) {

    fun findByDocAndAttribute(doc: Doc, attribute: Attribute) = attributeValueRepository.findByDocAndAttribute(doc, attribute)
}