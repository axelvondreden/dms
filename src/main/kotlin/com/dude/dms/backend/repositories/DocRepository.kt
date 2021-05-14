package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc

interface DocRepository : RestoreRepository<Doc> {

    fun findByGuid(guid: String): Doc?

    fun findByTagsAndDeletedFalse(tag: Tag): Set<Doc>

    fun countByTagsAndDeletedFalse(tag: Tag): Long

    fun findByAttributeValues_AttributeEqualsAndDeletedFalse(attribute: Attribute): Set<Doc>

    fun countByAttributeValues_AttributeEqualsAndDeletedFalse(attribute: Attribute): Long

    fun findByDocTextIsNull(): Set<Doc>
}