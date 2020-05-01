package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.mails.Mail
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
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
            " AND (:attribute is null or :attribute IN (SELECT av.attribute FROM AttributeValue av WHERE av.doc = doc))" +
            " AND (:mail is null or :mail = doc.mail)")
    fun findByFilter(
            @Param("tag") tag: Tag?,
            @Param("attribute") attribute: Attribute?,
            @Param("mail") mail: Mail?,
            pageable: Pageable
    ): Page<Doc>

    @Query("SELECT doc FROM Doc doc WHERE doc.deleted = false" +
            " AND (:tag is null or :tag MEMBER OF doc.tags)" +
            " AND (:attribute is null or :attribute IN (SELECT av.attribute FROM AttributeValue av WHERE av.doc = doc))" +
            " AND (:mail is null or :mail = doc.mail)")
    fun findByFilter(
            @Param("tag") tag: Tag?,
            @Param("attribute") attribute: Attribute?,
            @Param("mail") mail: Mail?,
            sort: Sort
    ): Set<Doc>

    fun findByDeletedIsNull(): Set<Doc>
}