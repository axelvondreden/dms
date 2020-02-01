package com.dude.dms.backend.service

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.LogsEvents
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
import org.springframework.data.jpa.repository.JpaRepository

abstract class EventService<T>(
        repository: JpaRepository<T, Long>,
        private val eventManager: EventManager
) : CrudService<T>(repository) where T : DataEntity, T : LogsEvents{

    override fun create(entity: T): T {
        val new = super.create(entity)
        if (entity.showEvents()) LOGGER.showInfo("Created $new") else LOGGER.info("Created $new")
        eventManager.trigger(new, EventType.CREATE)
        return new
    }

    override fun save(entity: T): T {
        val new = super.save(entity)
        if (entity.showEvents()) LOGGER.showInfo("Updated $new") else LOGGER.info("Updated $new")
        eventManager.trigger(new, EventType.UPDATE)
        return new
    }

    override fun delete(entity: T) {
        super.delete(entity)
        if (entity.showEvents()) LOGGER.showInfo("Deleted $entity") else LOGGER.info("Deleted $entity")
        eventManager.trigger(entity, EventType.DELETE)
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(EventService::class.java)
    }
}