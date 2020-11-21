package com.dude.dms.brain.parsing.search

abstract class Value: Translatable

object True : Value() {
    override fun translate() = "true"
}
object False : Value() {
    override fun translate() = "true"
}
data class IntLiteral(val value: Int) : Value() {
    override fun translate() = value.toString()
}
data class StringLiteral(val value: String) : Value() {
    override fun translate() = "'$value'"
}
data class ArrayLiteral(val value: List<StringLiteral>) : Value() {
    override fun translate() = value.joinToString(", ") { it.translate() }
}
