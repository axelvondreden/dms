package com.dude.dms.backend.service

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.Historical
import com.dude.dms.backend.data.history.History
import com.dude.dms.brain.events.EventManager
import org.springframework.data.jpa.repository.JpaRepository

abstract class HistoryCrudService<T, U : History>(
        jpaRepository: JpaRepository<U, Long>,
        eventManager: EventManager
) : EventService<U>(jpaRepository, eventManager) where T : DataEntity, T : Historical<U> {
    abstract fun getHistory(entity: T): List<U>
}