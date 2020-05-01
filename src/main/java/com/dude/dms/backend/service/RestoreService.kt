package com.dude.dms.backend.service

import com.dude.dms.backend.data.LogsEvents
import com.dude.dms.backend.data.RestorableEntity
import com.dude.dms.backend.repositories.RestoreRepository
import com.dude.dms.brain.events.EventManager

abstract class RestoreService<T>(
        private val restoreRepository: RestoreRepository<T>,
        eventManager: EventManager
) : EventService<T>(restoreRepository, eventManager) where T : RestorableEntity, T : LogsEvents{

    override fun findAll() = restoreRepository.findByDeletedFalse()

    override fun count() = restoreRepository.countByDeletedFalse()

    fun findDeleted() = restoreRepository.findByDeletedTrue()

    fun countDeleted() = restoreRepository.countByDeletedTrue()

    open fun restore(entity: T) {
        entity.deleted = false
        save(entity)
    }

    override fun softDelete(entity: T) {
        entity.deleted = true
        save(entity)
        super.softDelete(entity)
    }
}