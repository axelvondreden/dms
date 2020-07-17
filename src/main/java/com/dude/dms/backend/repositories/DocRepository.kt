package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DocRepository : RestoreRepository<Doc> {

    fun findByGuid(guid: String): Doc?

    fun findByTagsAndDeletedFalse(tag: Tag): Set<Doc>

    fun countByTagsAndDeletedFalse(tag: Tag): Long

    fun findByAttributeValues_AttributeEqualsAndDeletedFalse(attribute: Attribute): Set<Doc>

    fun countByAttributeValues_AttributeEqualsAndDeletedFalse(attribute: Attribute): Long

    @Query("SELECT doc FROM Doc doc WHERE doc.deleted = false" +
            " AND (:tag is null or :tag MEMBER OF doc.tags)" +
            " AND (:attribute is null or :attribute IN (SELECT av.attribute FROM AttributeValue av WHERE av.doc = doc))")
    fun findByFilter(
            @Param("tag") includedTags: Set<Tag>?,
            @Param("attribute") includedAttributes: Set<Attribute>?,
            pageable: Pageable
    ): Page<Doc>
}