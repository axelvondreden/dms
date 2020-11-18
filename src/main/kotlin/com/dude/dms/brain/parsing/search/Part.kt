package com.dude.dms.brain.parsing.search

interface Part {
    fun get(): String

    fun debug() = "[${this::class.simpleName!!.replace("Part", "")}: ${get()}]"
}