package com.dude.dms.backend.containers

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Word
import com.dude.dms.brain.dsl.attributefilter.AttributeFilterLang
import com.dude.dms.brain.dsl.tagFilterLang.TagFilterLang
import com.dude.dms.brain.options.Options
import com.dude.dms.utils.docService
import com.dude.dms.utils.fileManager
import com.dude.dms.utils.pageService
import java.io.File
import java.time.LocalDate

class DocContainer(var guid: String, var file: File? = null) {

    constructor(doc: Doc) : this(doc.guid) {
        this.doc = doc
        tags = doc.tags.map { TagContainer(it) }.toMutableSet()
        date = doc.documentDate
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

    val lines: Set<LineContainer>
        get() = getPages().flatMap { it.lines }.toSet()

    val words: Set<WordContainer>
        get() = lines.flatMap { it.words }.toSet()

    val thumbnail: File
        get() = fileManager.getThumb(guid)

    fun getPages(): Set<PageContainer> {
        val pages = if (useOcrTxt) ocrPages else pdfPages
        if (doc != null && pages.isEmpty()) {
            setPages(pageService.findByDoc(doc!!).map { PageContainer(it) }.toSet())
            return if (useOcrTxt) ocrPages else pdfPages
        }
        return pages
    }

    fun setPages(pages: Set<PageContainer>) {
        if (useOcrTxt) ocrPages = pages else pdfPages = pages
    }

    private fun getLine(word: WordContainer) =
        word.word.line ?: doc?.getLine(word.word) ?: getPages().flatMap { it.lines }.first { word in it.words }.line

    fun getFullTextLowerCase() = doc?.let { docService.getFullTextLowerCase(it) } ?: getPages().sortedBy { it.nr }.joinToString("\n") { page ->
        page.lines.sortedBy { it.y }.joinToString("\n") { line ->
            line.words.sortedBy { it.word.x }.joinToString(" ") { it.word.text.toString() }
        }
    }.lowercase()

    fun getFullText() = doc?.getFullText() ?: getPages().sortedBy { it.nr }.joinToString("\n") { page ->
        page.lines.sortedBy { it.y }.joinToString("\n") { line ->
            line.words.sortedBy { it.word.x }.joinToString(" ") { it.word.text.toString() }
        }
    }

    fun checkTagFilter(filter: TagFilterLang.Query) = lines.firstOrNull { filterIsValid(filter, it) }

    private fun filterIsValid(filter: TagFilterLang.Query, line: LineContainer): Boolean = when (filter) {
        is TagFilterLang.Query.And -> filterIsValid(filter.left, line) && filterIsValid(filter.right, line)
        is TagFilterLang.Query.Or -> filterIsValid(filter.left, line) || filterIsValid(filter.right, line)
        is TagFilterLang.Filter.Line -> testFilter(line.getFullText(), filter.value.value, filter.op)
        is TagFilterLang.Filter.LineRegex -> testFilterRegex(line.getFullText(), filter.value.value, filter.op)
    }

    private fun testFilter(lineText: String, filterText: String, op: TagFilterLang.Operator) = when (op) {
        TagFilterLang.Operator.Contains -> lineText.contains(filterText, ignoreCase = true)
        TagFilterLang.Operator.NotContains -> !lineText.contains(filterText, ignoreCase = true)
    }

    private fun testFilterRegex(lineText: String, filterText: String, op: TagFilterLang.Operator) = when (op) {
        TagFilterLang.Operator.Contains -> Regex(".*$filterText.*", RegexOption.IGNORE_CASE).matches(lineText)
        TagFilterLang.Operator.NotContains -> !Regex(".*$filterText.*", RegexOption.IGNORE_CASE).matches(lineText)
    }

    fun getWordsForAttributeFilter(filter: AttributeFilterLang.Query) = words.filter { filterIsValid(filter, it) }.toSet()

    private fun filterIsValid(filter: AttributeFilterLang.Query, word: WordContainer): Boolean = when (filter) {
        is AttributeFilterLang.Query.And -> filterIsValid(filter.left, word) && filterIsValid(filter.right, word)
        is AttributeFilterLang.Query.Or -> filterIsValid(filter.left, word) || filterIsValid(filter.right, word)
        is AttributeFilterLang.Filter.CurrentWord -> testFilter(word.text, filter.value.value, filter.op)
        is AttributeFilterLang.Filter.NextWord -> {
            val words = getLine(word).words.sortedBy { it.x }
            val index = words.indexOf(word.word)
            index < words.size - 1 && testFilter(words[index + 1].text ?: "", filter.value.value, filter.op)
        }
        is AttributeFilterLang.Filter.PreviousWord -> {
            val words = getLine(word).words.sortedBy { it.x }
            val index = words.indexOf(word.word)
            index > 0 && testFilter(words[index - 1].text ?: "", filter.value.value, filter.op)
        }
        is AttributeFilterLang.Filter.Line -> testFilter(getLine(word).getFullText(), filter.value.value, filter.op)
    }

    private fun testFilter(wordText: String, filterText: String, op: AttributeFilterLang.Operator) = when (op) {
        AttributeFilterLang.Operator.Equal -> wordText.equals(filterText, ignoreCase = true)
        AttributeFilterLang.Operator.NotEqual -> !wordText.equals(filterText, ignoreCase = true)
        AttributeFilterLang.Operator.Contains -> wordText.contains(filterText, ignoreCase = true)
        AttributeFilterLang.Operator.NotContains -> !wordText.contains(filterText, ignoreCase = true)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DocContainer
        if (guid != other.guid) return false
        return true
    }

    override fun hashCode() = guid.hashCode()

    companion object {
        fun fromString(text: String) = DocContainer("temp").apply {
            setPages(
                setOf(
                    PageContainer(1, text.split("\n").mapIndexed { i, line ->
                        LineContainer(i.toFloat(), line.split(" ").map {
                            WordContainer(Word(null, it, i.toFloat(), i.toFloat(), i.toFloat(), i.toFloat()))
                        }.toSet())
                    }.toSet())
                )
            )
        }
    }
}
