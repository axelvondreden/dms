package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.mails.Mail
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MailRepository : RestoreRepository<Mail> {

    fun findByDocsAndDeletedFalse(doc: Doc): Set<Mail>

    fun countByDocsAndDeletedFalse(doc: Doc): Int

    @Query("SELECT mail FROM Mail mail WHERE mail.deleted = false AND (:tag is null or :tag MEMBER OF mail.tags) AND (:doc is null or :doc MEMBER OF mail.docs)")
    fun findByFilter(@Param("tag") tag: Tag?, @Param("doc") doc: Doc?, pageable: Pageable): Page<Mail>

    @Query("SELECT COUNT(*) FROM Mail mail WHERE mail.deleted = false AND (:tag is null or :tag MEMBER OF mail.tags) AND (:doc is null or :doc MEMBER OF mail.docs)")
    fun countByFilter(@Param("tag") tag: Tag?, @Param("doc") doc: Doc?): Long

    fun findByTagsAndDeletedFalse(tag: Tag): Set<Mail>

    fun countByTagsAndDeletedFalse(tag: Tag): Long
}