package com.dude.dms.brain.events

import com.dude.dms.backend.data.LogsEvents
import com.dude.dms.brain.DmsLogger
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class EventManager {

    private val listeners = HashSet<Event<LogsEvents>>()

    fun trigger(entity: LogsEvents, type: EventType) {
        listeners.filter { it.target == entity::class && it.type == type }.forEach {
            it.run(entity)
        }
        listeners.filter { it.target == entity::class && it.type == type }.distinctBy { it.holder.ui }.forEach {
            val ui = it.holder.ui
            val text = when(type) {
                EventType.CREATE -> "Created"
                EventType.UPDATE -> "Updated"
                EventType.DELETE -> "Deleted"
            }
            if (entity.showEvents() && ui.isPresent) {
                LOGGER.showInfo("$text $entity", ui.get())
            } else {
                LOGGER.info("$text $entity")
            }
        }
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