package com.dude.dms.brain.parsing.search

import parser4k.*
import parser4k.commonparsers.Tokens
import parser4k.commonparsers.joinedWith
import parser4k.commonparsers.token

object SearchLang {

    private val boolLiteral = oneOf(str("true"), str("false")).map { if (it == "true") True else False }
    private val intLiteral = Tokens.integer.map { IntLiteral(it.toInt()) }
    private val stringLiteral = Tokens.string.map { StringLiteral(it) }
    private val arrayLiteral = inOrder(token("["), ref { stringLiteral }.joinedWith(token(",")), token("]"))
            .skipWrapper().map(::ArrayLiteral)
    private val unaryMinus = inOrder(token("-"), ref { intLiteral }).map { (_, it) -> IntLiteral(-it.value) }

    private val textKey = token("text").map { TextKey }
    private val tagKey = token("tag").map { TagKey }

    private val equal = token("=").map { Equal }
    private val notEqual = token("!=").map { NotEqual }
    private val less = token("<").map { Less }
    private val greater = token(">").map { Greater }
    private val inArray = token("in").map { InArray }
    private val notInArray = token("!in").map { NotInArray }
    private val like = token("~=").map { Like }
    private val notLike = token("!~=").map { NotLike }

    private val textFilter = inOrder(textKey, oneOf(like, notLike), stringLiteral).map { TextFilter(it.second, it.third) }
    private val tagFilter = inOrder(
            tagKey,
            oneOf(
                    inOrder(equal, stringLiteral),
                    inOrder(notEqual, stringLiteral),
                    inOrder(inArray, arrayLiteral),
                    inOrder(notInArray, arrayLiteral)
            )
    ).map { TagFilter(it.second.first, it.second.second) }

    private val filter = oneOf(textFilter, tagFilter)

    private val and = inOrder(ref { query }, token("and"), ref { query }).map { And(it.first, it.third) }
    private val or = inOrder(ref { query }, token("or"), ref { query }).map { Or(it.first, it.third) }

    private val paren = inOrder(token("("), ref { query }, token(")")).skipWrapper()

    private val query: Parser<Query> = oneOfWithPrecedence(or, and, paren.nestedPrecedence(), filter)
    private val key: Parser<Key> = oneOf(textKey, tagKey)
    private val op: Parser<Operator> = oneOf(equal, notEqual, less, greater, inArray, notInArray, like, notLike)

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
        while (count < 5 && testKey == null) {
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
        while (count < 5 && testOp == null) {
            try {
                testOp = s.takeLast(count).parseWith(op)
            } catch (e: NoMatchingParsers) {
                count++
            } catch (e: InputIsNotConsumed) {
                count++
            }
        }
        return testOp to count
    }

    fun translate(query: Query): String = query.translate()
}