package com.dude.dms.brain.parsing.search

import com.dude.dms.brain.t
import com.dude.dms.utils.attributeService
import parser4k.InputIsNotConsumed
import parser4k.NoMatchingParsers

class SearchParser {

    data class ParseResult(val query: Query?, val error: String?, val isValid: Boolean)

    private var text: String = ""

    val filter
        get() = ""

    fun setInput(text: String): ParseResult {
        this.text = text
        return try {
            ParseResult(SearchLang.parse(text.trim()), null, true)
        } catch (e: NoMatchingParsers) {
            ParseResult(null, e.message, false)
        } catch (e: InputIsNotConsumed) {
            ParseResult(null, e.message, false)
        }
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
            val keyTest = SearchLang.testForKeyFromEnd(text.trim().dropLast(opTest.second))
            if (keyTest.first != null) {
                if (text.endsWith(" ")) return keyTest.first!!.getValueHints(opTest.first!!)
                val start = text.takeLast(keyTest.second)
                return keyTest.first!!.getValueHints(opTest.first!!).filter { it.text.startsWith(start, ignoreCase = true) }
            } else {
                if (text.endsWith(" ")) return opTest.first!!.hints
                val start = text.takeLast(opTest.second)
                return opTest.first!!.hints.filter { it.text.startsWith(start, ignoreCase = true) }
            }
        }
        val inKeyPart = Regex("^\\w*\$|^.*(${t("and")}\\W*|${t("or")}\\W*|\\()\\w*\$", RegexOption.IGNORE_CASE).matches(text)
        if (inKeyPart) {
            if (text.endsWith(" ")) return searchKeys
            val start = text.trim().split(Regex("\\s+")).last()
            return searchKeys.filter { it.text.startsWith(start, ignoreCase = true) }
        }
        val keyTest = SearchLang.testForKeyFromEnd(text.trim())
        if (keyTest.first != null) {
            if (text.endsWith(" ")) return keyTest.first!!.hints
            val start = text.takeLast(keyTest.second)
            return keyTest.first!!.hints.filter { it.text.startsWith(start, ignoreCase = true) }
        }
        return emptyList()
    }

    companion object {
        var attributes = attributeService.findAll().map { it.name }
        val searchKeys = listOf(
                Hint(t("text"), t("doc.text")),
                Hint(t("tag"), t("doc.tag")),
                Hint(t("date"), t("doc.date"))
        )//, *attributes.toTypedArray())
    }
}