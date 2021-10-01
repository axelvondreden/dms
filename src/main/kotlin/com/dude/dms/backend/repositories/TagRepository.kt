package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, Long> {

    fun findByName(name: String): Tag?

    fun findByAttributes(attribute: Attribute): Set<Tag>
}
