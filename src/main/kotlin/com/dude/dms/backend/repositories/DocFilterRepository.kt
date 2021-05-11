package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.filter.DocFilter
import org.springframework.data.jpa.repository.JpaRepository

interface DocFilterRepository : JpaRepository<DocFilter, Long> {

    fun findByName(name: String): DocFilter?
}