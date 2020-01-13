package com.dude.dms.backend.data

import org.springframework.core.GenericTypeResolver

interface Diffable<T : DataEntity?> {

    fun diff(other: T): String? {
        val diff = StringBuilder()
        val clazz = GenericTypeResolver.resolveTypeArgument(javaClass, Diffable::class.java)
        if (clazz != null) {
            val fields = clazz.declaredFields
            for (field in fields) {
                try {
                    if (field[this] != field[other]) {
                        diff.append(field.name).append(": ").append(field[this]).append(" -> ").append(field[other]).append('\n')
                    }
                } catch (e: IllegalAccessException) { // ignore private fields
                }
            }
        }
        return diff.toString()
    }
}