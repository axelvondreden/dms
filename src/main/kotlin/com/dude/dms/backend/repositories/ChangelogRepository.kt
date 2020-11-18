package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.Changelog
import org.springframework.data.jpa.repository.JpaRepository

interface ChangelogRepository : JpaRepository<Changelog, Long> {

    fun findByVersion(version: String): Changelog?
}