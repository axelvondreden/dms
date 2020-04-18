package com.dude.dms.backend.service

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.data.mails.Mail
import com.dude.dms.backend.repositories.DocRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.io.Serializable

@Service
class DocService(
        private val docRepository: DocRepository,
        private val attributeValueService: AttributeValueService,
        private val lineService: LineService,
        eventManager: EventManager
) : EventService<Doc>(docRepository, eventManager) {

    data class Filter(
            var tag: Tag? = null,
            var attribute: Attribute? = null,
            var mail: Mail? = null,
            var text: String? = null
    ) : Serializable

    fun create(entity: Doc, attributeValues: Set<AttributeValue>): Doc {
        val new = super.create(entity)
        attributeValues.forEach {
            it.doc = new
            attributeValueService.create(it)
        }
        return new
    }

    override fun create(entity: Doc): Doc {
        val new = super.create(entity)
        createAttributeValues(new)
        return new
    }

    override fun save(entity: Doc): Doc {
        createAttributeValues(entity)
        super.save(entity)
        deleteAttributeValues(entity)
        return entity
    }

    override fun delete(entity: Doc) {
        entity.lines.forEach(lineService::delete)
        entity.attributeValues.forEach(attributeValueService::delete)
        super.delete(entity)
    }

    private fun createAttributeValues(doc: Doc) {
        doc.tags.flatMap { it.attributes }
                .filter { attributeValueService.findByDocAndAttribute(doc, it) == null }
                .map { AttributeValue(doc, it) }
                .distinct()
                .forEach { attributeValueService.create(it) }
    }

    private fun deleteAttributeValues(doc: Doc) {
        val attributes = doc.tags.flatMap { it.attributes }
        doc.attributeValues
                .filter { it.attribute !in attributes }
                .distinct().forEach { attributeValueService.delete(it) }
    }

    fun findByGuid(guid: String): Doc? = docRepository.findByGuid(guid)

    fun findByTag(tag: Tag) = docRepository.findByTags(tag)

    fun countByTag(tag: Tag) = docRepository.countByTags(tag)

    fun findByAttribute(attribute: Attribute) = docRepository.findByAttributeValues_AttributeEquals(attribute)

    fun countByAttribute(attribute: Attribute) = docRepository.countByAttributeValues_AttributeEquals(attribute)

    fun findByFilter(filter: Filter, pageable: Pageable) = docRepository.findByFilter(filter.tag, filter.attribute, filter.mail, filter.text, pageable)

    fun findByFilter(filter: Filter, sort: Sort) = docRepository.findByFilter(filter.tag, filter.attribute, filter.mail, filter.text, sort)

    fun getFullTextMemory(lines: Set<Line>) = lines.sortedBy { it.y }.joinToString(" ") { line ->
        line.words.sortedBy { it.x }.joinToString(" ") { it.text }
    }

    fun getFullText(lines: Set<Line>) = lines.sortedBy { it.y }.joinToString(" ") { line ->
        line.words.sortedBy { it.x }.joinToString(" ") { it.text }
    }
}