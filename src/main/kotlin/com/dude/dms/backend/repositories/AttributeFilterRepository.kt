package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.filter.AttributeFilter
import org.springframework.data.jpa.repository.JpaRepository

interface AttributeFilterRepository : JpaRepository<AttributeFilter, Long> {

    fun findByAttribute(attribute: Attribute): AttributeFilter?
}
