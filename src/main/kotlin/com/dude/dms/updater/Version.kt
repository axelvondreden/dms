package com.dude.dms.updater

import java.util.*
import java.util.regex.Pattern
import kotlin.math.max

data class Version(var version: String) : Comparable<Version?> {

    init {
        if (!PATTERN.matcher(version).matches()) { // ugly fix for old version pattern (v0.0.1, ...)
            this.version = version.substring(1)
            require(PATTERN.matcher(this.version).matches()) { "Invalid version format" }
        }
    }

    fun isBefore(other: Version?) = compareTo(other) < 0

    fun isAfter(other: Version?) = compareTo(other) > 0

    override fun compareTo(other: Version?): Int {
        if (other == null) return 1
        val thisParts = version.split("\\.").toTypedArray()
        val thatParts = other.version.split("\\.").toTypedArray()
        val length = max(thisParts.size, thatParts.size)
        for (i in 0 until length) {
            val thisPart = if (i < thisParts.size) thisParts[i].toInt() else 0
            val thatPart = if (i < thatParts.size) thatParts[i].toInt() else 0
            if (thisPart < thatPart) return -1
            if (thisPart > thatPart) return 1
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        return if (javaClass != other.javaClass) false else compareTo(other as Version?) == 0
    }

    override fun hashCode() = Objects.hash(version)

    companion object {
        private val PATTERN = Pattern.compile("[0-9]+(\\.[0-9]+)*")
    }
}