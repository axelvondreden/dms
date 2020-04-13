package com.dude.dms.backend.containers

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.brain.options.Options
import java.io.File
import java.time.LocalDate

class DocContainer(var guid: String) {

    constructor(doc: Doc) : this(doc.guid) {
        this.doc = doc
        tags = doc.tags
        attributeValues = doc.attributeValues
        date = doc.documentDate
        inDB = true
        lines = doc.lines.map { LineContainer(it) }.toSet()
    }

    var doc: Doc? = null

    var tags: MutableSet<Tag> = mutableSetOf()
        set(value) {
            field = value
            doc?.let { it.tags = value }
        }

    var attributeValues: MutableSet<AttributeValue> = mutableSetOf()
        set(value) {
            field = value
            doc?.let { it.attributeValues = value }
        }

    val file: File? = null

    var language: String = Options.get().doc.ocrLanguage

    var useOcrTxt: Boolean = false

    var ocrText: Set<LineContainer> = emptySet()
    var pdfText: Set<LineContainer> = emptySet()

    var ocrSpelling: Float = 0F
    var pdfSpelling: Float = 0F

    var date: LocalDate? = null
        set(value) {
            field = value
            doc?.let { it.documentDate = value }
        }

    var inDB: Boolean = false

    var lines: Set<LineContainer>
        get() = if (useOcrTxt) ocrText else pdfText
        set(value) = if (useOcrTxt) ocrText = value else pdfText = value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DocContainer
        if (guid != other.guid) return false
        return true
    }

    override fun hashCode() = guid.hashCode()
}