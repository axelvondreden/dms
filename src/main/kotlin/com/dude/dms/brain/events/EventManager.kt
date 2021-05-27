package com.dude.dms.brain.events

import com.dude.dms.backend.data.LogsEvents
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class EventManager {

    private val listeners = HashSet<Event<LogsEvents>>()

    fun trigger(entity: LogsEvents, type: EventType) {
        listeners.filter { it.target == entity::class && it.type == type }.forEach { it.run(entity) }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : LogsEvents> register(holder: com.vaadin.flow.component.Component, target: KClass<T>, vararg types: EventType, func: (T) -> Unit) {
        types.forEach { type ->
            val event = Event(holder, target, type, func) as Event<LogsEvents>
            listeners.remove(event)
            listeners.add(event)
        }
    }

    fun unregister(holder: com.vaadin.flow.component.Component) {
        listeners.removeIf { it.holder == holder }
    }
}
