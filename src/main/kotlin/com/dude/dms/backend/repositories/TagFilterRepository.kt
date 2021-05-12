package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.filter.TagFilter
import org.springframework.data.jpa.repository.JpaRepository

interface TagFilterRepository : JpaRepository<TagFilter, Long> {

    fun findByTag(tag: Tag): TagFilter?
}