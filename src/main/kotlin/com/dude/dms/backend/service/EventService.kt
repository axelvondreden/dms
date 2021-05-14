package com.dude.dms.backend.service

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.LogsEvents
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
import org.springframework.data.jpa.repository.JpaRepository

abstract class EventService<T>(
        repository: JpaRepository<T, Long>,
        private val eventManager: EventManager
) : CrudService<T>(repository) where T : DataEntity, T : LogsEvents {

    override fun create(entity: T): T {
        val new = super.create(entity)
        eventManager.trigger(new, EventType.CREATE)
        return new
    }

    override fun save(entity: T): T {
        val new = super.save(entity)
        eventManager.trigger(new, EventType.UPDATE)
        return new
    }

    fun silentSave(entity: T) = super.save(entity)

    override fun delete(entity: T) {
        super.delete(entity)
        eventManager.trigger(entity, EventType.DELETE)
    }

    open fun softDelete(entity: T) {
        eventManager.trigger(entity, EventType.DELETE)
    }
}