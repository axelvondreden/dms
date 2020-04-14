package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.docs.Doc
import org.springframework.data.jpa.repository.JpaRepository

interface AttributeValueRepository : JpaRepository<AttributeValue, Long> {

    fun findByDocAndAttribute(doc: Doc, attribute: Attribute): AttributeValue?

    fun findByAttribute(attribute: Attribute): Set<AttributeValue>
}