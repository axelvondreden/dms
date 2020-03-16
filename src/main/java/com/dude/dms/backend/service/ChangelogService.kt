package com.dude.dms.backend.service

import com.dude.dms.backend.data.Changelog
import com.dude.dms.backend.repositories.ChangelogRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class ChangelogService(
        private val changelogRepository: ChangelogRepository,
        eventManager: EventManager
) : EventService<Changelog>(changelogRepository, eventManager) {

    fun findByVersion(version: String) = changelogRepository.findByVersion(version)

    override fun create(entity: Changelog) = findByVersion(entity.version) ?: super.create(entity)
}