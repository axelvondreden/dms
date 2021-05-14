package com.dude.dms.backend.containers

import com.dude.dms.backend.data.docs.Line

data class LineContainer(var y: Float, var words: Set<WordContainer> = emptySet()) {

    constructor(line: Line) : this(line.y, line.words.map { WordContainer(it) }.toSet()) {
        _line = line
    }

    private var _line: Line? = null
    val line: Line
        get() = _line ?: Line(null, words.map { it.word }.toMutableSet(), y)

    fun getFullText() = words.sortedBy { it.word.x }.joinToString(" ") { it.text }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LineContainer
        if (y != other.y) return false
        return true
    }

    override fun hashCode() = y.hashCode()
}