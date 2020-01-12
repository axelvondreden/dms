package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import org.springframework.data.jpa.repository.JpaRepository

interface DocRepository : JpaRepository<Doc, Long> {
    fun findByGuid(guid: String): Doc?
    fun findByTags(tag: Tag): List<Doc>
    fun countByTags(tag: Tag): Long
    fun findTop10ByRawTextContaining(rawText: String): List<Doc>
    fun countByRawTextContaining(rawText: String): Long
    fun findTop10ByRawTextContainingIgnoreCase(rawText: String): List<Doc>
    fun countByRawTextContainingIgnoreCase(rawText: String): Long
    fun countByAttributeValues_AttributeEquals(attribute: Attribute): Long
}