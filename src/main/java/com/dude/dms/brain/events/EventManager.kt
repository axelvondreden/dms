package com.dude.dms.brain.events

import com.dude.dms.backend.data.LogsEvents
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.t
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class EventManager {

    private val listeners = HashSet<Event<LogsEvents>>()

    fun trigger(entity: LogsEvents, type: EventType) {
        listeners.filter { it.target == entity::class && it.type == type }.forEach {
            it.run(entity)
        }
        val uis = listeners.filter { it.target == entity::class && it.type == type && it.holder.ui.isPresent }
                .map { it.holder.ui.get() }.toSet()
        val text = when(type) {
            EventType.CREATE -> "created"
            EventType.UPDATE -> "updated"
            EventType.DELETE -> "deleted"
        }
        if (entity.showEvents()) {
            uis.forEach { LOGGER.showInfo(t(text, entity), it, log = false) }
        }
        //LOGGER.info(t(text, entity))
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : LogsEvents> register(holder: com.vaadin.flow.component.Component, target: KClass<T>, vararg types: EventType, func: (T) -> Unit) {
        for (type in types) {
            val event = Event(holder, target, type, func) as Event<LogsEvents>
            listeners.remove(event)
            listeners.add(event)
        }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(EventManager::class.java)
    }
}