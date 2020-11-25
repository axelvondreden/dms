package com.dude.dms.brain.search

import com.dude.dms.brain.t
import parser4k.*
import parser4k.commonparsers.Tokens
import parser4k.commonparsers.joinedWith
import parser4k.commonparsers.token

object SearchLang {

    private val boolLiteral = oneOf(str("true"), str("false")).map { if (it == "true") True else False }
    private val intLiteral = Tokens.integer.map { IntLiteral(it.toInt()) }
    private val stringLiteral = Tokens.string.map(::StringLiteral)
    private val dateLiteral = inOrder(intLiteral, token("."), intLiteral, token("."), intLiteral).map { DateLiteral(it.val1.value, it.val3.value, it.val5.value) }
    private val arrayLiteral = inOrder(token("["), ref { stringLiteral }.joinedWith(token(",")), token("]"))
            .skipWrapper().map(::ArrayLiteral)
    private val unaryMinus = inOrder(token("-"), ref { intLiteral }).map { (_, it) -> IntLiteral(-it.value) }

    private val textKey = token(t("text")).map { TextKey }
    private val dateKey = token(t("date")).map { DateKey }
    private val createdKey = token(t("created")).map { CreatedKey }
    private val tagKey = token(t("tag")).map { TagKey }

    private val orderAsc = token(t("search.order.asc")).map { Asc }
    private val orderDesc = token(t("search.order.desc")).map { Desc }
    private val orderBy = inOrder(
            token(t("search.order")),
            token(t("search.order.by")),
            oneOf(dateKey, createdKey),
            oneOf(orderAsc, orderDesc)
    ).map { OrderBy(it.val3, it.val4) }

    private val equal = token("=").map { Equal }
    private val notEqual = token("!=").map { NotEqual }
    private val less = token("<").map { Less }
    private val greater = token(">").map { Greater }
    private val inArray = token("in").map { InArray }
    private val notInArray = token("!in").map { NotInArray }
    private val like = token("~=").map { Like }
    private val notLike = token("!~=").map { NotLike }

    private val textFilter = inOrder(textKey, oneOf(like, notLike), stringLiteral).map { TextFilter(it.second, it.third) }
    private val dateFilter = inOrder(dateKey, oneOf(equal, notEqual, less, greater), dateLiteral).map { DateFilter(it.second, it.third) }
    private val createdFilter = inOrder(createdKey, oneOf(equal, notEqual, less, greater), dateLiteral).map { CreatedFilter(it.second, it.third) }
    private val tagFilter = inOrder(
            tagKey,
            oneOf(
                    inOrder(equal, stringLiteral),
                    inOrder(notEqual, stringLiteral),
                    inOrder(inArray, arrayLiteral),
                    inOrder(notInArray, arrayLiteral)
            )
    ).map { TagFilter(it.second.first, it.second.second) }

    private val filter = oneOf(textFilter, tagFilter, dateFilter, createdFilter)

    private val and = inOrder(ref { query }, token(t("and")), ref { query }).map { And(it.first, it.third) }
    private val or = inOrder(ref { query }, token(t("or")), ref { query }).map { Or(it.first, it.third) }

    private val paren = inOrder(token("("), ref { query }, token(")")).skipWrapper()

    private val query: Parser<Query> = oneOfWithPrecedence(or, and, paren.nestedPrecedence(), filter)

    private val search: Parser<Search> = inOrder(optional(query), optional(orderBy)).map { Search(it.first, it.second) }

    private val key: Parser<Key> = oneOf(textKey, tagKey, dateKey, createdKey)
    private val orderKey: Parser<OrderKey> = oneOf(dateKey, createdKey)
    private val op: Parser<Operator> = oneOf(equal, notEqual, less, greater, inArray, notInArray, like, notLike)

    fun parse(s: String): Search = s.parseWith(search)

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
     * Tries to parse a valid order-key while increasing the amount if searched characters from the end of the string
     * @return [Pair] order-key and number of characters until a key could be parsed
     */
    fun testForOrderKeyFromEnd(s: String): Pair<OrderKey?, Int> {
        var count = 1
        var testKey: OrderKey? = null
        while (count < 10 && testKey == null) {
            try {
                testKey = s.takeLast(count).parseWith(orderKey)
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
}