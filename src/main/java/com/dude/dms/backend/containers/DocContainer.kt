package com.dude.dms.backend.containers

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Line
import com.dude.dms.brain.options.Options
import java.io.File
import java.time.LocalDate

class DocContainer(var guid: String, var file: File? = null) {

    constructor(doc: Doc) : this(doc.guid) {
        this.doc = doc
        tags = doc.tags
        date = doc.documentDate
        lines = doc.lines.map { LineContainer(it) }.toSet()
    }

    var done: Boolean = false

    var image: File? = null

    var doc: Doc? = null

    var tags: MutableSet<Tag> = mutableSetOf()
        set(value) {
            field = value
            if (doc != null) {
                doc!!.tags = value
                attributeValues = doc!!.attributeValues
            } else {
                attributeValues = value.flatMap { it.attributes }.map { AttributeValue(doc, it) }.distinct().toMutableSet()
            }
        }

    var attributeValues: MutableSet<AttributeValue> = mutableSetOf()

    var language: String = Options.get().doc.ocrLanguage

    var useOcrTxt: Boolean = false

    var ocrLines: Set<LineContainer> = emptySet()
    var pdfLines: Set<LineContainer> = emptySet()

    var date: LocalDate? = null
        set(value) {
            field = value
            doc?.let { it.documentDate = value }
        }

    val inDB: Boolean
        get() = doc != null

    var lines: Set<LineContainer>
        get() = if (useOcrTxt) ocrLines else pdfLines
        set(value) = if (useOcrTxt) ocrLines = value else pdfLines = value

    var lineEntities: Set<Line>
        get() = lines.map { it.line }.toSet()
        set(value) { lines = value.map { LineContainer(it) }.toSet() }

    val words: Set<WordContainer>
        get() = lines.flatMap { it.words }.toSet()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DocContainer
        if (guid != other.guid) return false
        return true
    }

    override fun hashCode() = guid.hashCode()
}