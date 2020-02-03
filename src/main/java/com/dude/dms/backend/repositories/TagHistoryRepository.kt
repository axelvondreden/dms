package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.history.TagHistory
import org.springframework.data.jpa.repository.JpaRepository

interface TagHistoryRepository : JpaRepository<TagHistory, Long> {

    fun findByTag(tag: Tag): Set<TagHistory>
}