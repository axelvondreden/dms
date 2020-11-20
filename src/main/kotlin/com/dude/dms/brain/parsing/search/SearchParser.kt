package com.dude.dms.brain.parsing.search

import com.dude.dms.extensions.attributeService
import parser4k.NoMatchingParsers

class SearchParser() {

    private var text: String = ""

    val filter
        get() = ""

    fun setInput(text: String): String? {
        this.text = text
        try {
            val op = SearchLang.parse(text.trim())
        } catch (e: NoMatchingParsers) {
            return e.message
        }
        return null
    }

    fun refresh() {
        attributes = attributeService.findAll().map { it.name }
        //tagIncludeFilter.setItems(tagService.findAll())
        //attributeIncludeFilter.setItems(attributeService.findAll())
    }

    fun getTips(): List<String> {
        if (text.count { it == '"' } % 2 != 0) {
            return emptyList()
        }
        if (text.isBlank()) {
            return searchKeys
        }
        val inValuePart = listOf("\\=", "\\!\\=", "\\<", "\\>", "in \\[", "not in \\[").any { Regex(".*$it(\\s*)?\\S*\$").matches(text) }
        if (inValuePart) {
            return emptyList()
        }
        val inKeyPart = Regex("^\\w*\$|^.*(and|or)\\W\\w*\$").matches(text)
        if (inKeyPart) {
            val start = text.trim().split(Regex("\\s+")).last()
            return searchKeys.filter { it.startsWith(start, ignoreCase = true) }
        }
        return listOf("and ", "or ")
    }

    companion object {
        var attributes = attributeService.findAll().map { it.name }
        val searchKeys = listOf("text", "date", "tag", *attributes.toTypedArray())
    }
}