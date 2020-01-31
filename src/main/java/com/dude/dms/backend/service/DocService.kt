package com.dude.dms.backend.service

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.history.DocHistory
import com.dude.dms.backend.repositories.DocRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class DocService(
        private val docRepository: DocRepository,
        private val attributeValueService: AttributeValueService,
        private val docHistoryService: DocHistoryService,
        private val textBlockService: TextBlockService,
        eventManager: EventManager
) : HistoricalCrudService<Doc, DocHistory>(docRepository, eventManager) {

    override fun create(entity: Doc): Doc {
        createAttributeValues(entity)
        return super.create(entity)
    }

    override fun save(entity: Doc): Doc {
        createAttributeValues(entity)
        super.save(entity)
        deleteAttributeValues(entity)
        return entity
    }

    override fun delete(entity: Doc) {
        docHistoryService.getHistory(entity).forEach(docHistoryService::delete)
        textBlockService.findByDoc(entity).forEach(textBlockService::delete)
        attributeValueService.findByDoc(entity).forEach(attributeValueService::delete)
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
        doc.attributeValues.filter { it.attribute !in attributes }.distinct()
                .forEach { attributeValueService.delete(it) }
    }

    override fun createHistory(entity: Doc, text: String?, created: Boolean, edited: Boolean) = DocHistory(entity, text, created, edited)

    fun findByGuid(guid: String): Doc? = docRepository.findByGuid(guid)

    fun findByTag(tag: Tag) = docRepository.findByTags(tag)

    fun countByTag(tag: Tag) = docRepository.countByTags(tag)

    fun findTop10ByRawTextContaining(rawText: String) = docRepository.findTop10ByRawTextContaining(rawText)

    fun countByRawTextContaining(rawText: String) = docRepository.countByRawTextContaining(rawText)

    fun findTop10ByRawTextContainingIgnoreCase(rawText: String) = docRepository.findTop10ByRawTextContainingIgnoreCase(rawText)

    fun countByRawTextContainingIgnoreCase(rawText: String) = docRepository.countByRawTextContainingIgnoreCase(rawText)

    fun countByAttribute(attribute: Attribute) = docRepository.countByAttributeValues_AttributeEquals(attribute)
}