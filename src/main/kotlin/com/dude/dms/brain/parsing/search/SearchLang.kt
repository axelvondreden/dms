package com.dude.dms.brain.parsing.search

import com.dude.dms.brain.parsing.search.Filter.*
import com.dude.dms.brain.parsing.search.Query.*
import com.dude.dms.brain.parsing.search.Value.ArrayLiteral
import parser4k.*
import parser4k.commonparsers.Tokens
import parser4k.commonparsers.joinedWith
import parser4k.commonparsers.token

object SearchLang {

    private val boolLiteral = oneOf(str("true"), str("false")).map { if (it == "true") Value.True else Value.False }
    private val intLiteral = Tokens.integer.map { Value.IntLiteral(it.toInt()) }
    private val stringLiteral = Tokens.string.map { Value.StringLiteral(it) }
    private val arrayLiteral = inOrder(token("["), ref { stringLiteral }.joinedWith(token(",")), token("]"))
            .skipWrapper().map(::ArrayLiteral)
    private val unaryMinus = inOrder(token("-"), ref { intLiteral }).map { (_, it) -> Value.IntLiteral(-it.value) }

    private val key = oneOf(SearchParser.searchKeys.map { str(it) }).map { Key(it) }
    private val value = oneOf(boolLiteral, intLiteral, stringLiteral, arrayLiteral, unaryMinus)

    private val equal = inOrder(ref { key }, token("=="), ref { value }).map { Equal(it.first, it.third) }
    private val notEqual = inOrder(ref { key }, token("!="), ref { value }).map { NotEqual(it.first, it.third) }
    private val less = inOrder(ref { key }, token("<"), ref { value }).map { Less(it.first, it.third) }
    private val greater = inOrder(ref { key }, token(">"), ref { value }).map { Greater(it.first, it.third) }

    private val inArray = inOrder(ref { key }, token("in"), ref { value }).map { InArray(it.first, it.third) }
    private val notInArray = inOrder(ref { key }, token("not in"), ref { value }).map { NotInArray(it.first, it.third) }

    private val filter = oneOf(equal, notEqual, less, greater, inArray, notInArray)

    private val and = inOrder(ref { query }, token("and"), ref { query }).map { And(it.first, it.third) }
    private val or = inOrder(ref { query }, token("or"), ref { query }).map { Or(it.first, it.third) }

    private val paren = inOrder(token("("), ref { query }, token(")")).skipWrapper()

    private val query: Parser<Query> = oneOfWithPrecedence(or, and, paren.nestedPrecedence(), filter)

    fun parse(s: String): Query = s.parseWith(query)

    fun translate(query: Query): String = query.translate()
}