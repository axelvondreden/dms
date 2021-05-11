package com.dude.dms.brain.dsl.docsearch

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.brain.dsl.hint.Hint
import com.dude.dms.brain.dsl.hint.HintResult
import com.dude.dms.brain.t
import com.dude.dms.utils.attributeService
import com.vaadin.flow.component.icon.VaadinIcon
import parser4k.InputIsNotConsumed
import parser4k.NoMatchingParsers

class DocSearchParser {

    data class ParseResult(val docSearch: DocSearchLang.Search?, val error: String?, val isValid: Boolean)

    private var text: String = ""

    val filter
        get() = ""

    fun setInput(text: String): ParseResult {
        this.text = text
        return try {
            ParseResult(DocSearchLang.parse(text.trim()), null, true)
        } catch (e: NoMatchingParsers) {
            ParseResult(null, e.message, false)
        } catch (e: InputIsNotConsumed) {
            ParseResult(null, e.message, false)
        }
    }

    //TODO: refactor
    fun getHints(): HintResult {
        if (text.count { it == '"' } % 2 != 0) {
            return HintResult(emptyList())
        }
        if (text.isBlank()) {
            return HintResult(searchKeys.plus(Hint("${t("search.order")} ${t("search.order.by")}", t("sort"))))
        }
        val inOrderBy = text.contains(Regex("${t("search.order")}\\W*${t("search.order.by")}"))
        if (inOrderBy) {
            val inKeyPart = Regex("^.*${t("search.order")}\\W*${t("search.order.by")}\\W*\\w*\$", RegexOption.IGNORE_CASE).matches(text)
            if (inKeyPart) {
                if (text.endsWith(" ")) return HintResult(orderKeys)
                val start = text.trim().split(Regex("\\s+")).last()
                return HintResult(orderKeys.filter { it.text.startsWith(start, ignoreCase = true) }, true)
            }
            val keyTest = DocSearchLang.testForOrderKeyFromEnd(text.trim())
            if (keyTest.first != null) {
                if (text.endsWith(" ")) return HintResult(keyTest.first!!.orderHints)
                val start = text.takeLast(keyTest.second)
                return HintResult(keyTest.first!!.orderHints.filter { it.text.startsWith(start, ignoreCase = true) }, true)
            }
        } else {
            val queryTest = DocSearchLang.testForQueryDroppingLast(text.trim())
            if (queryTest.first != null) {
                if (text.endsWith(" ")) return HintResult(queryTest.first!!.hints)
                val start = text.takeLast(queryTest.second)
                return HintResult(queryTest.first!!.hints.filter { it.text.startsWith(start, ignoreCase = true) }, true)
            }
            val opTest = DocSearchLang.testForOpFromEnd(text.trim())
            if (opTest.first != null) {
                val keyTest = DocSearchLang.testForKeyFromEnd(text.trim().dropLast(opTest.second))
                if (keyTest.first != null) {
                    if (text.endsWith(" ")) return HintResult(keyTest.first!!.getValueHints(opTest.first!!))
                    val start = text.takeLast(keyTest.second)
                    return HintResult(keyTest.first!!.getValueHints(opTest.first!!).filter { it.text.startsWith(start, ignoreCase = true) }, true)
                } else {
                    if (text.endsWith(" ")) return HintResult(opTest.first!!.hints)
                    val start = text.takeLast(opTest.second)
                    return HintResult(opTest.first!!.hints.filter { it.text.startsWith(start, ignoreCase = true) }, true)
                }
            }
            //TODO: clean up
            @Suppress("RegExpDuplicateAlternationBranch")
            val inKeyPart = Regex("^\\w*\$|^.*(${t("and")}\\W*|${t("or")}\\W*|\\()\\w*\$", RegexOption.IGNORE_CASE).matches(text)
            if (inKeyPart) {
                if (text.endsWith(" ")) return HintResult(searchKeys)
                val start = text.trim().split(Regex("\\s+")).last()
                return HintResult(searchKeys.filter { it.text.startsWith(start, ignoreCase = true) }, true)
            }
            val keyTest = DocSearchLang.testForKeyFromEnd(text.trim())
            if (keyTest.first != null) {
                if (text.endsWith(" ")) return HintResult(keyTest.first!!.hints)
                val start = text.takeLast(keyTest.second)
                return HintResult(keyTest.first!!.hints.filter { it.text.startsWith(start, ignoreCase = true) }, true)
            }
        }
        return HintResult(emptyList())
    }

    companion object {
        val searchKeys = listOf(
                Hint(t("text"), t("doc.text")),
                Hint(t("tag"), t("doc.tag")),
                Hint(t("date"), t("doc.date")),
                Hint(t("created"), t("doc.insert.date"))
        ).plus(attributeService.findAll().map {
            val param = when (it.type) {
                Attribute.Type.STRING -> "${t("attribute")} (${t("text")})" to VaadinIcon.TEXT_LABEL
                Attribute.Type.INT -> "${t("attribute")} (Integer)" to VaadinIcon.HASH
                Attribute.Type.FLOAT -> "${t("attribute")} (${t("number")})" to VaadinIcon.SUPERSCRIPT
                Attribute.Type.DATE -> "${t("attribute")} (${t("date")})" to VaadinIcon.CALENDAR
            }
            Hint(it.name, param.first, icon = param.second)
        })
        val orderKeys = listOf(
                Hint(t("date"), t("doc.date")),
                Hint(t("created"), t("doc.insert.date"))
        )
    }
}