package com.dude.dms.brain.dsl.attributefilter

import com.dude.dms.brain.dsl.hint.Hint
import com.dude.dms.brain.dsl.hint.Hints
import com.dude.dms.brain.t
import parser4k.*
import parser4k.commonparsers.Tokens
import parser4k.commonparsers.token

object AttributeFilterLang {

    private val stringLiteral = Tokens.string.map { Value.String(it) }

    private val currentWordKey = token("this").map { Key.CurrentWord }
    private val nextWordKey = token("next").map { Key.NextWord }
    private val previousWordKey = token("previous").map { Key.PreviousWord }
    private val lineKey = token("line").map { Key.Line }

    private val equal = token("=").map { Operator.Equal }
    private val notEqual = token("!=").map { Operator.NotEqual }
    private val contains = token("~=").map { Operator.Contains }
    private val notContains = token("!~=").map { Operator.NotContains }

    private val op = oneOf(equal, notEqual, contains, notContains)

    private val currentWordFilter = inOrder(currentWordKey, op, stringLiteral).map { Filter.CurrentWord(it.second, it.third) }
    private val nextWordFilter = inOrder(nextWordKey, op, stringLiteral).map { Filter.NextWord(it.second, it.third) }
    private val previousWordFilter = inOrder(previousWordKey, op, stringLiteral).map { Filter.PreviousWord(it.second, it.third) }
    private val lineFilter = inOrder(lineKey, op, stringLiteral).map { Filter.Line(it.second, it.third) }

    private val filter = oneOf(currentWordFilter, nextWordFilter, previousWordFilter, lineFilter)

    private val and = inOrder(ref { query }, token("and"), ref { query }).map { Query.And(it.first, it.third) }
    private val or = inOrder(ref { query }, token("or"), ref { query }).map { Query.Or(it.first, it.third) }

    private val paren = inOrder(token("("), ref { query }, token(")")).skipWrapper()

    private val query: Parser<Query> = oneOfWithPrecedence(or, and, paren.nestedPrecedence(), filter)

    private val key: Parser<Key> = oneOf(currentWordKey, nextWordKey, previousWordKey, lineKey)

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
        data class CurrentWord(val op: Operator, val value: Value.String) : Filter()
        data class NextWord(val op: Operator, val value: Value.String) : Filter()
        data class PreviousWord(val op: Operator, val value: Value.String) : Filter()
        data class Line(val op: Operator, val value: Value.String) : Filter()
    }

    sealed class Key : Hints {
        override val hints
            get() = listOf(
                Hint("=", t("search.equal")),
                Hint("!=", t("search.notequal")),
                Hint("~=", t("search.like")),
                Hint("!~=", t("search.notlike"))
            )

        fun getValueHints() = listOf(Hint("\"\"", t("text"), 1))

        object CurrentWord : Key()
        object NextWord : Key()
        object PreviousWord : Key()
        object Line : Key()
    }

    sealed class Operator : Hints {
        override val hints get() = listOf(Hint("\"\"", t("text"), 1))

        object Equal : Operator()
        object NotEqual : Operator()
        object Contains : Operator()
        object NotContains : Operator()
    }

    sealed class Query : Hints {
        override val hints get() = listOf(Hint("and"), Hint("or"))

        data class And(val left: Query, val right: Query) : Query()
        data class Or(val left: Query, val right: Query) : Query()
    }

    sealed class Value {
        data class String(val value: kotlin.String)
    }
}
