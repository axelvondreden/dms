package com.dude.dms.brain.dsl.tagFilterLang

import com.dude.dms.brain.dsl.hint.Hint
import com.dude.dms.brain.dsl.hint.HintResult
import com.dude.dms.brain.t
import parser4k.InputIsNotConsumed
import parser4k.NoMatchingParsers

class TagFilterParser {

    data class ParseResult(val tagFilter: TagFilterLang.Query?, val error: String?, val isValid: Boolean)

    private var text: String = ""

    val filter
        get() = ""

    fun setInput(text: String): ParseResult {
        this.text = text
        return try {
            ParseResult(TagFilterLang.parse(text.trim()), null, true)
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
            return HintResult(filterKeys)
        }
        val queryTest = TagFilterLang.testForQueryDroppingLast(text.trim())
        if (queryTest.first != null) {
            if (text.endsWith(" ")) return HintResult(queryTest.first!!.hints)
            val start = text.takeLast(queryTest.second)
            return HintResult(queryTest.first!!.hints.filter { it.text.startsWith(start, ignoreCase = true) }, true)
        }
        val opTest = TagFilterLang.testForOpFromEnd(text.trim())
        if (opTest.first != null) {
            val keyTest = TagFilterLang.testForKeyFromEnd(text.trim().dropLast(opTest.second))
            if (keyTest.first != null) {
                if (text.endsWith(" ")) return HintResult(keyTest.first!!.getValueHints())
                val start = text.takeLast(keyTest.second)
                return HintResult(
                    keyTest.first!!.getValueHints()
                        .filter { it.text.startsWith(start, ignoreCase = true) }, true
                )
            } else {
                if (text.endsWith(" ")) return HintResult(opTest.first!!.hints)
                val start = text.takeLast(opTest.second)
                return HintResult(opTest.first!!.hints.filter { it.text.startsWith(start, ignoreCase = true) }, true)
            }
        }
        //TODO: clean up
        @Suppress("RegExpDuplicateAlternationBranch")
        val inKeyPart = Regex("^\\w*\$|^.*(and\\W*|or\\W*|\\()\\w*\$", RegexOption.IGNORE_CASE).matches(text)
        if (inKeyPart) {
            if (text.endsWith(" ")) return HintResult(filterKeys)
            val start = text.trim().split(Regex("\\s+")).last()
            return HintResult(filterKeys.filter { it.text.startsWith(start, ignoreCase = true) }, true)
        }
        val keyTest = TagFilterLang.testForKeyFromEnd(text.trim())
        if (keyTest.first != null) {
            if (text.endsWith(" ")) return HintResult(keyTest.first!!.hints)
            val start = text.takeLast(keyTest.second)
            return HintResult(keyTest.first!!.hints.filter { it.text.startsWith(start, ignoreCase = true) }, true)
        }
        return HintResult(emptyList())
    }

    companion object {
        val filterKeys = listOf(Hint("line", t("line.current")))
    }
}