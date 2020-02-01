package com.dude.dms.brain.events

import com.dude.dms.backend.data.LogsEvents
import kotlin.reflect.KClass

data class Event<T : LogsEvents>(val clazz: KClass<*>, val target: KClass<T>, val type: EventType, val func: (T) -> Unit) {

    fun run(entity: T) = func.invoke(entity)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event<*>

        if (clazz != other.clazz) return false
        if (target != other.target) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clazz.hashCode()
        result = 31 * result + target.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}