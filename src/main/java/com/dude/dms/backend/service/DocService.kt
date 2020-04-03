package com.dude.dms.backend.service

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.*
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
        private val tagService: TagService,
        private val attributeService: AttributeService,
        private val attributeValueService: AttributeValueService,
        private val lineService: LineService,
        private val wordService: WordService,
        eventManager: EventManager
) : EventService<Doc>(docRepository, eventManager) {

    data class Filter(
            var tag: Tag? = null,
            var attribute: Attribute? = null,
            var mail: Mail? = null,
            var text: String? = null
    ) : Serializable

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
        lineService.findByDoc(entity).forEach(lineService::delete)
        attributeValueService.findByDoc(entity).forEach(attributeValueService::delete)
        super.delete(entity)
    }

    private fun createAttributeValues(doc: Doc) {
        tagService.findByDoc(doc).flatMap { attributeService.findByTag(it) }
                .filter { attributeValueService.findByDocAndAttribute(doc, it) == null }
                .map { AttributeValue(doc, it) }
                .distinct()
                .forEach { attributeValueService.create(it) }
    }

    private fun deleteAttributeValues(doc: Doc) {
        val attributes = tagService.findByDoc(doc).flatMap { attributeService.findByTag(it) }
        attributeValueService.findByDoc(doc)
                .filter { attributeService.findByAttributeValue(it) !in attributes }
                .distinct().forEach { attributeValueService.delete(it) }
    }

    fun findByGuid(guid: String): Doc? = docRepository.findByGuid(guid)

    fun findByTag(tag: Tag) = docRepository.findByTags(tag)

    fun countByTag(tag: Tag) = docRepository.countByTags(tag)

    fun findTop10ByRawTextContaining(rawText: String) = docRepository.findTop10ByRawTextContaining(rawText)

    fun countByRawTextContaining(rawText: String) = docRepository.countByRawTextContaining(rawText)

    fun findTop10ByRawTextContainingIgnoreCase(rawText: String) = docRepository.findTop10ByRawTextContainingIgnoreCase(rawText)

    fun countByRawTextContainingIgnoreCase(rawText: String) = docRepository.countByRawTextContainingIgnoreCase(rawText)

    fun findByAttribute(attribute: Attribute) = docRepository.findByAttributeValues_AttributeEquals(attribute)

    fun countByAttribute(attribute: Attribute) = docRepository.countByAttributeValues_AttributeEquals(attribute)

    fun findByFilter(filter: Filter, pageable: Pageable) = docRepository.findByFilter(filter.tag, filter.attribute, filter.mail, filter.text, pageable)

    fun findByFilter(filter: Filter, sort: Sort) = docRepository.findByFilter(filter.tag, filter.attribute, filter.mail, filter.text, sort)

    fun countByFilter(filter: Filter) = docRepository.countByFilter(filter.tag, filter.attribute, filter.mail, filter.text)

    fun getFullTextMemory(lines: Set<Line>) = lines.sortedBy { it.y }.joinToString(" ") { line ->
        line.words.sortedBy { it.x }.joinToString(" ") { it.text }
    }

    fun getFullText(lines: Set<Line>) = lines.sortedBy { it.y }.joinToString(" ") { line ->
        wordService.findByLine(line).sortedBy { it.x }.joinToString(" ") { it.text }
    }
}