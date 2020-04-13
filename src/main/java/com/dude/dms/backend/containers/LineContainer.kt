package com.dude.dms.backend.containers

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Line

data class LineContainer(var y: Float, var words: Set<WordContainer> = emptySet()) {

    constructor(line: Line) : this(line.y, line.words.map { WordContainer(it) }.toSet()) {
        this.line = line
    }

    var line: Line? = null

    fun toLine(doc: Doc? = null) = Line(doc, words.map { it.word }.toSet(), y)
}