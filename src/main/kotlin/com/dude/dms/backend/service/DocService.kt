package com.dude.dms.backend.service

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.DocText
import com.dude.dms.backend.repositories.DocRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import javax.persistence.EntityManager

@Service
class DocService(
    private val docRepository: DocRepository,
    private val attributeValueService: AttributeValueService,
    private val pageService: PageService,
    private val docTextService: DocTextService,
    eventManager: EventManager,
    private val entityManager: EntityManager
) : RestoreService<Doc>(docRepository, eventManager) {

    fun create(entity: Doc, attributeValues: Set<AttributeValue>): Doc {
        val new = super.create(entity)
        attributeValues.forEach {
            it.doc = new
            attributeValueService.create(it)
        }
        val text = DocText(new, getFullTextLowerCase(new))
        docTextService.create(text)
        new.docText = text
        super.save(new)
        return new
    }

    override fun create(entity: Doc): Doc {
        val new = super.create(entity)
        createAttributeValues(new)
        val text = DocText(new, getFullTextLowerCase(new))
        docTextService.save(text)
        return new
    }

    override fun save(entity: Doc): Doc {
        createAttributeValues(entity)
        super.save(entity)
        deleteAttributeValues(entity)
        val text = entity.docText
        if (text != null) {
            text.text = getFullTextLowerCase(entity)
            docTextService.save(text)
        } else {
            val new = DocText(entity, getFullTextLowerCase(entity))
            entity.docText = docTextService.create(new)
            super.save(entity)
        }
        return entity
    }

    override fun softDelete(entity: Doc) {
        pageService.findByDoc(entity).forEach(pageService::softDelete)
        entity.attributeValues.forEach(attributeValueService::softDelete)
        entity.docText?.let { docTextService.softDelete(it) }
        super.softDelete(entity)
    }

    override fun restore(entity: Doc) {
        pageService.findByDoc(entity).forEach(pageService::restore)
        entity.attributeValues.forEach(attributeValueService::restore)
        entity.docText?.let { docTextService.restore(it) }
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

    fun findByGuid(guid: String) = docRepository.findByGuid(guid)

    fun findByGuidAsSet(guid: String) = docRepository.findByGuidEquals(guid)

    fun findByTag(tag: Tag) = docRepository.findByTagsAndDeletedFalse(tag)

    fun findByTagNot(tag: Tag) = docRepository.findByTagsNotContainingAndDeletedFalse(tag)

    fun countByTag(tag: Tag) = docRepository.countByTagsAndDeletedFalse(tag)

    fun findByAttribute(attribute: Attribute) = docRepository.findByAttributeValues_AttributeEqualsAndDeletedFalse(attribute)

    fun countByAttribute(attribute: Attribute) = docRepository.countByAttributeValues_AttributeEqualsAndDeletedFalse(attribute)

    fun findByFilter(filter: String, pageable: Pageable): Set<Doc> =
        if (filter.isBlank()) findAll(pageable).toSet()
        else entityManager.createQuery("SELECT distinct doc FROM Doc doc LEFT JOIN doc.tags tag LEFT JOIN doc.attributeValues av $filter", Doc::class.java).apply {
            firstResult = pageable.offset.toInt()
            maxResults = pageable.pageSize
        }.resultList.toSet()

    fun countByFilter(filter: String): Long {
        if (filter.isBlank()) return count()
        //HACK
        val filterWithoutOrder = filter.replace(Regex("order by.*", RegexOption.IGNORE_CASE), "")
        return entityManager.createQuery("SELECT COUNT(distinct doc) FROM Doc doc LEFT JOIN doc.tags tag LEFT JOIN doc.attributeValues av $filterWithoutOrder").singleResult as Long
    }

    fun getFullTextLowerCase(doc: Doc) = pageService.findByDoc(doc).sortedBy { it.nr }.joinToString("\n") { it.getFullText() }.lowercase()
}
