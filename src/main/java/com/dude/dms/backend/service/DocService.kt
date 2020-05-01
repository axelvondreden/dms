package com.dude.dms.backend.service

import com.dude.dms.backend.containers.PageContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Page
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
        private val pageService: PageService,
        eventManager: EventManager
) : RestoreService<Doc>(docRepository, eventManager) {

    data class Filter(
            var tag: Tag? = null,
            var attribute: Attribute? = null,
            var mail: Mail? = null
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
        entity.pages.forEach(pageService::delete)
        entity.attributeValues.forEach(attributeValueService::delete)
        load(entity.id)?.let { super.delete(it) }
    }

    override fun softDelete(entity: Doc) {
        entity.pages.forEach(pageService::softDelete)
        entity.attributeValues.forEach(attributeValueService::softDelete)
        super.softDelete(entity)
    }

    override fun restore(entity: Doc) {
        entity.pages.forEach(pageService::restore)
        entity.attributeValues.forEach(attributeValueService::restore)
        super.restore(entity)
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

    fun findByTag(tag: Tag) = docRepository.findByTagsAndDeletedFalse(tag)

    fun countByTag(tag: Tag) = docRepository.countByTagsAndDeletedFalse(tag)

    fun findByAttribute(attribute: Attribute) = docRepository.findByAttributeValues_AttributeEqualsAndDeletedFalse(attribute)

    fun countByAttribute(attribute: Attribute) = docRepository.countByAttributeValues_AttributeEqualsAndDeletedFalse(attribute)

    fun findByFilter(filter: Filter, pageable: Pageable) = docRepository.findByFilter(filter.tag, filter.attribute, filter.mail, pageable)

    fun findByFilter(filter: Filter, sort: Sort) = docRepository.findByFilter(filter.tag, filter.attribute, filter.mail, sort)

    fun getFullText(pages: Set<Page>) = pages.sortedBy { it.nr }.joinToString("\n") { page ->
        page.lines.sortedBy { it.y }.joinToString("\n") { line ->
            line.words.sortedBy { it.x }.joinToString(" ") { it.text }
        }
    }

    fun getFullText2(pages: Set<PageContainer>) = pages.sortedBy { it.nr }.joinToString("\n") { page ->
        page.lines.sortedBy { it.y }.joinToString("\n") { line ->
            line.words.sortedBy { it.word.x }.joinToString(" ") { it.word.text }
        }
    }
}