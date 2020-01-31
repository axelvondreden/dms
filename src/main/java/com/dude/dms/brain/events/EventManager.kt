package com.dude.dms.brain.events

import com.dude.dms.backend.data.DataEntity
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class EventManager {

    private val listeners = HashSet<Event<DataEntity>>()

    fun trigger(entity: DataEntity, type: EventType) {
        listeners.filter { it.target == entity::class && it.type == type }.forEach { it.run(entity) }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : DataEntity> register(clazz: KClass<*>, target: KClass<T>, vararg types: EventType, func: (T) -> Unit) {
        for (type in types) {
            listeners.add(Event(clazz, target, type, func) as Event<DataEntity>)
        }
    }
}