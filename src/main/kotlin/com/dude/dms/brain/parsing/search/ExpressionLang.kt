package com.dude.dms.brain.parsing.search

import com.dude.dms.brain.parsing.search.ExpressionLang.Filter.*
import com.dude.dms.brain.parsing.search.ExpressionLang.Op.And
import com.dude.dms.brain.parsing.search.ExpressionLang.Op.Or
import com.dude.dms.brain.parsing.search.ExpressionLang.Value.ArrayLiteral
import com.dude.dms.brain.parsing.search.ExpressionLang.Value.UnaryMinus
import com.dude.dms.brain.parsing.search.ExpressionLang.parse
import parser4k.*
import parser4k.commonparsers.Tokens
import parser4k.commonparsers.joinedWith
import parser4k.commonparsers.token

object ExpressionLang {

    private val boolLiteral = oneOf(str("true"), str("false")).map { if (it == "true") Value.True else Value.False }
    private val intLiteral = Tokens.integer.map { Value.IntLiteral(it.toInt()) }
    private val stringLiteral = Tokens.string.map { Value.StringLiteral(it) }
    private val arrayLiteral = inOrder(token("["), ref { stringLiteral }.joinedWith(token(",")), token("]"))
            .skipWrapper().map(::ArrayLiteral)

    private val key = oneOf(str("a"), str("b"), str("c"), str("d")).map { Key(it) }
    private val value = oneOf(boolLiteral, intLiteral, stringLiteral, arrayLiteral)

    private val equal = inOrder(ref { key }, token("=="), ref { value }).map { Equal(it.first, it.third) }
    private val notEqual = inOrder(ref { key }, token("!="), ref { value }).map { NotEqual(it.first, it.third) }
    private val less = inOrder(ref { key }, token("<"), ref { value }).map { Less(it.first, it.third) }
    private val greater = inOrder(ref { key }, token(">"), ref { value }).map { Greater(it.first, it.third) }

    private val unaryMinus = inOrder(token("-"), ref { intLiteral }).map { (_, it) -> Value.IntLiteral(-it.value) }

    private val inArray = inOrder(ref { key }, token("in"), ref { value }).map { InArray(it.first, it.third) }
    private val notInArray = inOrder(ref { key }, token("not in"), ref { value }).map { NotInArray(it.first, it.third) }

    private val filter = oneOf(equal, notEqual, less, greater, inArray, notInArray)

    private val and = inOrder(ref { op }, token("and"), ref { op }).map { And(it.first, it.third) }
    private val or = inOrder(ref { op }, token("or"), ref { op }).map { Or(it.first, it.third) }

    private val paren = inOrder(token("("), ref { op }, token(")")).skipWrapper()

    private val op: Parser<Op> = oneOfWithPrecedence(or, and, paren.nestedPrecedence(), filter)

    fun parse(s: String): Op = s.parseWith(op)

    fun translate(op: Op): String = op.translate1()

    open class Op {
        data class And(val left: Op, val right: Op) : Op()
        data class Or(val left: Op, val right: Op) : Op()
    }

    data class Key(val value: String)

    sealed class Filter: Op() {
        data class Equal(val key: Key, val value: Value) : Filter()
        data class NotEqual(val key: Key, val value: Value) : Filter()
        data class Less(val key: Key, val value: Value) : Filter()
        data class Greater(val key: Key, val value: Value) : Filter()
        data class InArray(val key: Key, val value: Value) : Filter()
        data class NotInArray(val key: Key, val value: Value) : Filter()
    }

    sealed class Value {
        object True : Value()
        object False : Value()
        data class IntLiteral(val value: Int) : Value()
        data class StringLiteral(val value: String) : Value()
        data class ArrayLiteral(val value: List<StringLiteral>) : Value()

        data class UnaryMinus(val value: IntLiteral) : Value()
    }

    private fun Value.translate(): String =
            when (this) {
                Value.True -> "true"
                Value.False -> "false"
                is Value.IntLiteral -> value.toString()
                is Value.StringLiteral -> "'$value'"
                is ArrayLiteral -> value.joinToString(", ") { it.translate() }
                is UnaryMinus -> "-$value"
            }

    private fun Op.translate1(): String =
            when (this) {
                is Equal -> "(" + key.value + "=" + value.translate() + ")"
                is NotEqual -> "(" + key.value + "!=" + value.translate() + ")"
                is Less -> "(" + key.value + "<" + value.translate() + ")"
                is Greater -> "(" + key.value + ">" + value.translate() + ")"
                is InArray -> "(" + key.value + " in(" + value.translate() + "))"
                is NotInArray -> "(" + key.value + " not in(" + value.translate() + "))"

                is And -> "(" + left.translate1() + " and " + right.translate1() + ")"
                is Or -> "(" + left.translate1() + " or " + right.translate1() + ")"
                else -> throw RuntimeException("oh no wtf is happening")
            }
}

fun main() {
    val op = parse("a < 2 and (b == \"asd\" or c != 3 or d in [\"asd\", \"dsa\"])")
    println(ExpressionLang.translate(op))
}