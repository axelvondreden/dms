package com.dude.dms.backend.containers

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Page
import com.dude.dms.brain.options.Options
import java.io.File
import java.time.LocalDate

class DocContainer(var guid: String, var file: File? = null) {

    constructor(doc: Doc) : this(doc.guid) {
        this.doc = doc
        tags = doc.tags.map { TagContainer(it) }.toMutableSet()
        date = doc.documentDate
        pages = doc.pages.map { PageContainer(it) }.toSet()
    }

    var done: Boolean = false

    var doc: Doc? = null

    var tags: Set<TagContainer> = mutableSetOf()
        set(value) {
            field = value
            if (doc != null) {
                doc!!.tags = value.map { it.tag }.toMutableSet()
                attributeValues = doc!!.attributeValues.toMutableSet()
            }
            if (!value.isNullOrEmpty()) {
                attributeValues.removeIf { av -> value.none { av.attribute in it.tag.attributes } }
                attributeValues.addAll(
                        value.flatMap { it.tag.attributes }
                                .filter { attribute -> attribute !in attributeValues.map { it.attribute } }
                                .map { AttributeValue(doc, it) }.distinct()
                )
            }
        }

    var tagEntities: Set<Tag>
        get() = tags.map { it.tag }.toSet()
        set(value) {
            tags = value.map { TagContainer(it) }.toSet()
        }

    var attributeValues: MutableSet<AttributeValue> = mutableSetOf()

    var language: String = Options.get().doc.ocrLanguage

    var useOcrTxt: Boolean = false

    var ocrPages: Set<PageContainer> = emptySet()
    var pdfPages: Set<PageContainer> = emptySet()

    var date: LocalDate? = null
        set(value) {
            field = value
            doc?.let { it.documentDate = value }
        }

    val inDB: Boolean
        get() = doc != null

    var pages: Set<PageContainer>
        get() = if (useOcrTxt) ocrPages else pdfPages
        set(value) = if (useOcrTxt) ocrPages = value else pdfPages = value

    var pageEntities: Set<Page>
        get() = pages.map { it.page }.toSet()
        set(value) {
            pages = value.map { PageContainer(it) }.toSet()
        }

    val words: Set<WordContainer>
        get() = pages.flatMap { it.lines }.flatMap { it.words }.toSet()

    var thumbnail: File
        get() = pages.first { it.nr == 1 }.image!!
        set(value) {
            pages.first { it.nr == 1 }.image = value
        }

    fun getLine(word: WordContainer) = doc?.getLine(word.word) ?: pages.flatMap { it.lines }.first { word in it.words }.line

    fun getFullText() = doc?.getFullText() ?: pages.sortedBy { it.nr }.joinToString("\n") { page ->
        page.lines.sortedBy { it.y }.joinToString("\n") { line ->
            line.words.sortedBy { it.word.x }.joinToString(" ") { it.word.text.toString() }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DocContainer
        if (guid != other.guid) return false
        return true
    }

    override fun hashCode() = guid.hashCode()
}