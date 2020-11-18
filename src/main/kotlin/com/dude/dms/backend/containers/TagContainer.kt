package com.dude.dms.backend.containers

import com.dude.dms.backend.data.Tag

data class TagContainer(val tag: Tag, val tagOrigin: String? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TagContainer
        if (tag != other.tag) return false
        return true
    }

    override fun hashCode() = tag.hashCode()
}