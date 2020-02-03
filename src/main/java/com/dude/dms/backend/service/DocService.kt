package com.dude.dms.backend.service

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.TextBlock
import com.dude.dms.backend.data.history.DocHistory
import com.dude.dms.backend.repositories.DocRepository
import com.dude.dms.brain.events.EventManager
import com.dude.dms.ui.dataproviders.DocDataProvider
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class DocService(
        private val docRepository: DocRepository,
        private val tagService: TagService,
        private val attributeService: AttributeService,
        private val attributeValueService: AttributeValueService,
        private val docHistoryService: DocHistoryService,
        private val textBlockService: TextBlockService,
        eventManager: EventManager
) : HistoricalCrudService<Doc, DocHistory>(docRepository, eventManager) {

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
        docHistoryService.getHistory(entity).forEach(docHistoryService::delete)
        textBlockService.findByDoc(entity).forEach(textBlockService::delete)
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

    override fun createHistory(entity: Doc, text: String?, created: Boolean, edited: Boolean) = DocHistory(entity, text, created, edited)

    fun findByGuid(guid: String): Doc? = docRepository.findByGuid(guid)

    fun findByTag(tag: Tag) = docRepository.findByTags(tag)

    fun countByTag(tag: Tag) = docRepository.countByTags(tag)

    fun findTop10ByRawTextContaining(rawText: String) = docRepository.findTop10ByRawTextContaining(rawText)

    fun countByRawTextContaining(rawText: String) = docRepository.countByRawTextContaining(rawText)

    fun findTop10ByRawTextContainingIgnoreCase(rawText: String) = docRepository.findTop10ByRawTextContainingIgnoreCase(rawText)

    fun countByRawTextContainingIgnoreCase(rawText: String) = docRepository.countByRawTextContainingIgnoreCase(rawText)

    fun findByAttribute(attribute: Attribute) = docRepository.findByAttributeValues_AttributeEquals(attribute)

    fun countByAttribute(attribute: Attribute) = docRepository.countByAttributeValues_AttributeEquals(attribute)

    fun findByFilter(filter: DocDataProvider.Filter, pageable: Pageable) = docRepository.findByFilter(filter.tag, filter.mail, pageable)

    fun countByFilter(filter: DocDataProvider.Filter) = docRepository.countByFilter(filter.tag, filter.mail)

    fun findByTextBlock(textBlock: TextBlock) = docRepository.findByTextBlocks(textBlock)
}