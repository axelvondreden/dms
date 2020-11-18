package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface DocRepository : RestoreRepository<Doc> {

    fun findByGuid(guid: String): Doc?

    fun findByTagsAndDeletedFalse(tag: Tag): Set<Doc>

    fun countByTagsAndDeletedFalse(tag: Tag): Long

    fun findByAttributeValues_AttributeEqualsAndDeletedFalse(attribute: Attribute): Set<Doc>

    fun countByAttributeValues_AttributeEqualsAndDeletedFalse(attribute: Attribute): Long

    @Query("""
            SELECT doc
            FROM Doc doc
            WHERE doc.deleted = false
            AND (:from IS NULL OR doc.documentDate >= :from)
            AND (:to IS NULL OR doc.documentDate <= :to)
            AND (
                (:includedTags) IS NULL
                OR
                (
                    :includeAllTags = false 
                    AND 
                    (SELECT COUNT(tag) FROM doc.tags tag WHERE tag IN (:includedTags)) > 0
                )
                OR
                (
                    :includeAllTags = true 
                    AND 
                    (SELECT COUNT (*) FROM doc.tags) > 0
                    AND
                    (SELECT COUNT(tag) FROM doc.tags tag WHERE tag IN (:includedTags)) 
                    = (SELECT COUNT(*) FROM Tag tag WHERE tag IN (:includedTags))
                )
            )
            AND (
                (:includedAttributes) IS NULL
                OR
                (
                    :includeAllAttributes = false 
                    AND 
                    (SELECT COUNT(av.attribute) FROM AttributeValue av WHERE av.doc = doc AND av.attribute IN (:includedAttributes)) > 0
                )
                OR
                (
                    :includeAllAttributes = true 
                    AND
                    (SELECT COUNT(av.attribute) FROM AttributeValue av WHERE av.doc = doc) > 0
                    AND
                    (SELECT COUNT(av.attribute) FROM AttributeValue av WHERE av.doc = doc AND av.attribute IN (:includedAttributes))
                    = (SELECT COUNT(*) FROM Attribute a WHERE a IN (:includedAttributes))
                )
            )
            AND (
                (:excludedTags) IS NULL
                OR
                (SELECT COUNT(tag) FROM doc.tags tag WHERE tag IN (:excludedTags)) = 0
            )
            AND (
                (:excludedAttributes) IS NULL
                OR
                (SELECT Count(av.attribute) FROM AttributeValue av WHERE av.doc = doc AND av.attribute IN (:excludedAttributes)) = 0
            )"""
    )
    fun findByFilter(
            @Param("from") from: LocalDate?,
            @Param("to") to: LocalDate?,
            @Param("includeAllTags") includeAllTags: Boolean,
            @Param("includeAllAttributes") includeAllAttributes: Boolean,
            @Param("includedTags") includedTags: Set<Tag>?,
            @Param("includedAttributes") includedAttributes: Set<Attribute>?,
            @Param("excludedTags") excludedTags: Set<Tag>?,
            @Param("excludedAttributes") excludedAttributes: Set<Attribute>?,
            pageable: Pageable
    ): Page<Doc>
}