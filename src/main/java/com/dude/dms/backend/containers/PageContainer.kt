package com.dude.dms.backend.containers

import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.data.docs.Page
import java.io.File

data class PageContainer(var nr: Int, var lines: Set<LineContainer> = emptySet()) {

    constructor(page: Page) : this(page.nr, page.lines.map { LineContainer(it) }.toSet()) {
        _page = page
    }

    private var _page: Page? = null
    val page: Page
        get() = _page ?: Page(null, lines.map { it.line }.toMutableSet(), nr)

    var image: File? = null

    var lineEntities: Set<Line>
        get() = lines.map { it.line }.toSet()
        set(value) { lines = value.map { LineContainer(it) }.toSet() }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PageContainer
        if (nr != other.nr) return false
        return true
    }

    override fun hashCode() = nr.hashCode()
}