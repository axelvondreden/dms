package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.mails.Mail
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DocRepository : JpaRepository<Doc, Long> {

    fun findByGuid(guid: String): Doc?

    fun findByTags(tag: Tag): Set<Doc>

    fun countByTags(tag: Tag): Long

    fun findByAttributeValues_AttributeEquals(attribute: Attribute): Set<Doc>

    fun countByAttributeValues_AttributeEquals(attribute: Attribute): Long

    @Query("SELECT doc FROM Doc doc WHERE (:tag is null or :tag MEMBER OF doc.tags)" +
            " AND (:attribute is null or :attribute IN (SELECT av.attribute FROM AttributeValue av WHERE av.doc = doc))" +
            " AND (:mail is null or :mail = doc.mail)" +
            " AND (:text is null or LOWER(doc.rawText) LIKE LOWER(CONCAT('%', :text, '%')))")
    fun findByFilter(
            @Param("tag") tag: Tag?,
            @Param("attribute") attribute: Attribute?,
            @Param("mail") mail: Mail?,
            @Param("text") text: String?,
            pageable: Pageable
    ): Page<Doc>

    @Query("SELECT doc FROM Doc doc WHERE (:tag is null or :tag MEMBER OF doc.tags)" +
            " AND (:attribute is null or :attribute IN (SELECT av.attribute FROM AttributeValue av WHERE av.doc = doc))" +
            " AND (:mail is null or :mail = doc.mail)" +
            " AND (:text is null or LOWER(doc.rawText) LIKE LOWER(CONCAT('%', :text, '%')))")
    fun findByFilter(
            @Param("tag") tag: Tag?,
            @Param("attribute") attribute: Attribute?,
            @Param("mail") mail: Mail?,
            @Param("text") text: String?,
            sort: Sort
    ): Set<Doc>
}