package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import org.springframework.data.jpa.repository.JpaRepository

interface AttributeRepository : JpaRepository<Attribute, Long> {

    fun findByName(name: String): Attribute?

    fun findByTags(tag: Tag): Set<Attribute>

    fun countByTags(tag: Tag): Long

    fun findByAttributeValues(attributeValue: AttributeValue): Attribute
}