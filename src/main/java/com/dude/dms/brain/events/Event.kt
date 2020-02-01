package com.dude.dms.brain.events

import com.dude.dms.backend.data.LogsEvents
import com.vaadin.flow.component.Component
import kotlin.reflect.KClass

data class Event<T : LogsEvents>(val holder: Component, val target: KClass<T>, val type: EventType, val func: (T) -> Unit) {

    fun run(entity: T) = func.invoke(entity)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event<*>

        if (holder::class != other.holder::class) return false
        if (target != other.target) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = holder::class.hashCode()
        result = 31 * result + target.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}