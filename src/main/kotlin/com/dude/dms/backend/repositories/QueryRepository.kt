package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Query
import org.springframework.data.jpa.repository.JpaRepository

interface QueryRepository : JpaRepository<Query, Long> {

    fun findByName(name: String): Query?
}