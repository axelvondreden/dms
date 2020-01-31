package com.dude.dms.backend.service

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.Diffable
import com.dude.dms.backend.data.Historical
import com.dude.dms.backend.data.history.History
import com.dude.dms.brain.events.EventManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository

abstract class HistoricalCrudService<T, U : History>(
        jpaRepository: JpaRepository<T, Long>,
        eventManager: EventManager
) : EventService<T>(jpaRepository, eventManager) where T : DataEntity, T : Historical<U>, T : Diffable<T> {

    @Autowired
    private lateinit var historyService: HistoryCrudService<T, U>

    protected abstract fun createHistory(entity: T, text: String?, created: Boolean, edited: Boolean): U

    override fun save(entity: T): T {
        val before = load(entity.id)
        val after = super.save(entity)
        historyService.create(createHistory(after, before?.diff(after), created = false, edited = true))
        return after
    }

    override fun create(entity: T): T {
        val created = super.create(entity)
        historyService.create(createHistory(created, null, created = true, edited = false))
        return created
    }
}