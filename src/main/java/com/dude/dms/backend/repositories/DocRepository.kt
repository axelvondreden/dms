package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.mails.Mail
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DocRepository : JpaRepository<Doc, Long> {

    fun findByGuid(guid: String): Doc?

    fun findByTags(tag: Tag): List<Doc>

    fun countByTags(tag: Tag): Long

    fun findTop10ByRawTextContaining(rawText: String): List<Doc>

    fun countByRawTextContaining(rawText: String): Long

    fun findTop10ByRawTextContainingIgnoreCase(rawText: String): List<Doc>

    fun countByRawTextContainingIgnoreCase(rawText: String): Long

    fun countByAttributeValues_AttributeEquals(attribute: Attribute): Long

    @Query("SELECT doc FROM Doc doc WHERE (:tag is null or :tag MEMBER OF doc.tags) AND (:mail is null or :mail = doc.mail)")
    fun findByFilter(@Param("tag") tag: Tag?, @Param("mail") mail: Mail?, pageable: Pageable): Page<Doc>

    @Query("SELECT COUNT(*) FROM Doc doc WHERE (:tag is null or :tag MEMBER OF doc.tags) AND (:mail is null or :mail = doc.mail)")
    fun countByFilter(@Param("tag") tag: Tag?, @Param("mail") mail: Mail?): Long
}