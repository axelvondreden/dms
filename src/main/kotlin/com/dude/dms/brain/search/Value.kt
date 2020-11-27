package com.dude.dms.brain.search

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

data class FloatLiteral(val value: Double) : Value() {
    override fun translate() = value.toString()
}

data class StringLiteral(val value: String) : Value() {
    override fun translate() = "'$value'"
}

data class StringLikeLiteral(val value: String) : Value() {
    override fun translate() = "'$value'"
}

data class TagLiteral(val value: String) : Value() {
    override fun translate() = "'${value}'"
}

data class DateLiteral(val day: Int, val month: Int, val year: Int) : Value() {
    override fun translate() = "'$year-$month-$day'"
}

data class StringArrayLiteral(val value: List<StringLiteral>) : Value() {
    override fun translate() = "(${value.joinToString(", ") { it.translate() }})"
}

data class TagArrayLiteral(val value: List<TagLiteral>) : Value() {
    override fun translate() = "(${value.joinToString(", ") { it.translate() }})"
}
