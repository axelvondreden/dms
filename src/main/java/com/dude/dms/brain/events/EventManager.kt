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
    fun <T : LogsEvents> register(clazz: KClass<*>, target: KClass<T>, vararg types: EventType, func: (T) -> Unit) {
        for (type in types) {
            listeners.add(Event(clazz, target, type, func) as Event<LogsEvents>)
        }
    }
}