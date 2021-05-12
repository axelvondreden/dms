package com.dude.dms.brain.dsl.tagFilterLang

import com.dude.dms.brain.dsl.hint.Hint
import com.dude.dms.brain.dsl.hint.Hints
import com.dude.dms.brain.t
import parser4k.*
import parser4k.commonparsers.Tokens
import parser4k.commonparsers.token

object TagFilterLang {

    private val stringLiteral = Tokens.string.map { Value.String(it) }
    private val regexLiteral = inOrder(token("r("), Tokens.string, token(")")).map { Value.Regex(it.second) }

    private val lineKey = token("line").map { Key.Line }

    private val contains = token("~=").map { Operator.Contains }
    private val notContains = token("!~=").map { Operator.NotContains }

    private val op = oneOf(contains, notContains)

    private val lineFilter = inOrder(lineKey, op, stringLiteral).map { Filter.Line(it.second, it.third) }
    private val lineRegexFilter = inOrder(lineKey, op, regexLiteral).map { Filter.LineRegex(it.second, it.third) }

    private val filter = oneOf(lineFilter, lineRegexFilter)

    private val and = inOrder(ref { query }, token("and"), ref { query }).map { Query.And(it.first, it.third) }
    private val or = inOrder(ref { query }, token("or"), ref { query }).map { Query.Or(it.first, it.third) }

    private val paren = inOrder(token("("), ref { query }, token(")")).skipWrapper()

    private val query: Parser<Query> = oneOfWithPrecedence(or, and, paren.nestedPrecedence(), filter)

    private val key: Parser<Key> = oneOf(lineKey)

    fun parse(s: String): Query = s.parseWith(query)

    /**
     * Tries to parse a valid query while dropping the last character continuously
     * @return [Pair] query and number of dropped characters until a query could be parsed
     */
    fun testForQueryDroppingLast(s: String): Pair<Query?, Int> {
        var count = 0
        var copy = s
        var testQuery: Query? = null
        while (testQuery == null && copy.isNotEmpty() && copy.last() != ' ') {
            try {
                testQuery = copy.parseWith(query)
            } catch (e: NoMatchingParsers) {
                count++
                copy = copy.dropLast(1)
            } catch (e: InputIsNotConsumed) {
                count++
                copy = copy.dropLast(1)
            }
        }
        return testQuery to count
    }

    /**
     * Tries to parse a valid key while increasing the amount if searched characters from the end of the string
     * @return [Pair] key and number of characters until a key could be parsed
     */
    fun testForKeyFromEnd(s: String): Pair<Key?, Int> {
        var count = 1
        var testKey: Key? = null
        while (count < 10 && testKey == null) {
            try {
                testKey = s.takeLast(count).parseWith(key)
            } catch (e: NoMatchingParsers) {
                count++
            } catch (e: InputIsNotConsumed) {
                count++
            }
        }
        return testKey to count
    }

    /**
     * Tries to parse a valid operator while increasing the amount if searched characters from the end of the string
     * @return [Pair] operator and number of characters until an operator could be parsed
     */
    fun testForOpFromEnd(s: String): Pair<Operator?, Int> {
        var count = 1
        var testOp: Operator? = null
        var resultCount = 0
        while (count < 5) {
            try {
                testOp = s.takeLast(count).parseWith(op)
                resultCount = count
            } catch (e: NoMatchingParsers) {
            } catch (e: InputIsNotConsumed) {
            }
            count++
        }
        return testOp to resultCount
    }

    sealed class Filter : Query() {
        data class Line(val op: Operator, val value: Value.String) : Filter()
        data class LineRegex(val op: Operator, val value: Value.Regex) : Filter()
    }

    sealed class Key : Hints {
        override val hints
            get() = listOf(
                Hint("~=", t("search.like")),
                Hint("!~=", t("search.notlike"))
            )

        fun getValueHints() = listOf(
            Hint("\"\"", t("text"), 1),
            Hint("r(\"\")", "Regex", 2)
        )

        object Line : Key()
    }

    sealed class Operator : Hints {
        override val hints get() = listOf(
            Hint("\"\"", t("text"), 1),
            Hint("r(\"\")", "Regex", 2)
        )

        object Contains : Operator()
        object NotContains : Operator()
    }

    sealed class Query : Hints {
        override val hints get() = listOf(Hint(t("and")), Hint(t("or")))

        data class And(val left: Query, val right: Query) : Query()
        data class Or(val left: Query, val right: Query) : Query()
    }

    sealed class Value {
        data class String(val value: kotlin.String)
        data class Regex(val value: kotlin.String)
    }
}
