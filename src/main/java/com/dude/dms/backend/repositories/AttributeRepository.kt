package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.docs.Attribute
import org.springframework.data.jpa.repository.JpaRepository

interface AttributeRepository : JpaRepository<Attribute, Long> {
    fun findByName(name: String): Attribute?
}