package com.dude.dms.brain.parsing.search

open class Op {
    data class And(val left: Op, val right: Op) : Op()
    data class Or(val left: Op, val right: Op) : Op()
}

sealed class Filter : Op() {
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

data class Key(val value: String)

fun Op.translate(): String = when (this) {
    is Op.And -> "(" + left.translate() + " and " + right.translate() + ")"
    is Op.Or -> "(" + left.translate() + " or " + right.translate() + ")"
    is Filter.Equal -> "(" + key.value + "=" + value.translate() + ")"
    is Filter.NotEqual -> "(" + key.value + "!=" + value.translate() + ")"
    is Filter.Less -> "(" + key.value + "<" + value.translate() + ")"
    is Filter.Greater -> "(" + key.value + ">" + value.translate() + ")"
    is Filter.InArray -> "(" + key.value + " in(" + value.translate() + "))"
    is Filter.NotInArray -> "(" + key.value + " not in(" + value.translate() + "))"
    else -> throw RuntimeException("oh no wtf is happening")
}

fun Value.translate(): String = when (this) {
    Value.True -> "true"
    Value.False -> "false"
    is Value.IntLiteral -> value.toString()
    is Value.StringLiteral -> "'$value'"
    is Value.ArrayLiteral -> value.joinToString(", ") { it.translate() }
    is Value.UnaryMinus -> "-${value}"
}