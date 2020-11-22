package com.dude.dms.brain.parsing.search

import com.dude.dms.utils.attributeService
import parser4k.InputIsNotConsumed
import parser4k.NoMatchingParsers

class SearchParser {

    private var text: String = ""

    val filter
        get() = ""

    fun setInput(text: String): String? {
        this.text = text
        try {
            val op = SearchLang.parse(text.trim())
        } catch (e: NoMatchingParsers) {
            return e.message
        } catch (e: InputIsNotConsumed) {
            return e.message
        }
        return null
    }

    fun refresh() {
        attributes = attributeService.findAll().map { it.name }
        //tagIncludeFilter.setItems(tagService.findAll())
        //attributeIncludeFilter.setItems(attributeService.findAll())
    }

    fun getHints(): List<Hint> {
        if (text.count { it == '"' } % 2 != 0) {
            return emptyList()
        }
        if (text.isBlank()) {
            return searchKeys
        }
        val queryTest = SearchLang.testForQueryDroppingLast(text.trim())
        if (queryTest.first != null) {
            if (text.endsWith(" ")) return queryTest.first!!.hints
            val start = text.takeLast(queryTest.second)
            return queryTest.first!!.hints.filter { it.text.startsWith(start, ignoreCase = true) }
        }
        val opTest = SearchLang.testForOpFromEnd(text.trim())
        if (opTest.first != null) {
            if (text.endsWith(" ")) return opTest.first!!.hints
            val start = text.takeLast(opTest.second)
            return opTest.first!!.hints.filter { it.text.startsWith(start, ignoreCase = true) }
        }
        val inKeyPart = Regex("^\\w*\$|^.*(and\\W*|or\\W*|\\()\\w*\$", RegexOption.IGNORE_CASE).matches(text)
        if (inKeyPart) {
            if (text.endsWith(" ")) return searchKeys
            val start = text.trim().split(Regex("\\s+")).last()
            return searchKeys.filter { it.text.startsWith(start, ignoreCase = true) }
        }
        val keyTest = SearchLang.testForKeyFromEnd(text.trim())
        if (keyTest.first != null) {
            if (text.endsWith(" ")) return keyTest.first!!.hints
            val start = text.takeLast(keyTest.second)
            return queryTest.first!!.hints.filter { it.text.startsWith(start, ignoreCase = true) }
        }
        return listOf(Hint("???"))
    }

    companion object {
        var attributes = attributeService.findAll().map { it.name }
        val searchKeys = listOf(
                Hint("text", "Search in document text"),
                Hint("tag", "Search for document tags")
        )//, *attributes.toTypedArray())
    }
}