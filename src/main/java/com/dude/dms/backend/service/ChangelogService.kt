package com.dude.dms.backend.service

import com.dude.dms.backend.brain.DmsLogger.Companion.getLogger
import com.dude.dms.backend.data.Changelog
import com.dude.dms.backend.repositories.ChangelogRepository
import org.springframework.stereotype.Service

@Service
class ChangelogService(private val changelogRepository: ChangelogRepository) : CrudService<Changelog>(changelogRepository) {

    fun findByVersion(version: String) = changelogRepository.findByVersion(version)

    override fun create(entity: Changelog): Changelog {
        val current = findByVersion(entity.version)
        return if (current != null) {
            LOGGER.warn("Tried to create changelog with same Version")
            current
        } else super.create(entity)
    }

    companion object {
        private val LOGGER = getLogger(ChangelogService::class.java)
    }
}