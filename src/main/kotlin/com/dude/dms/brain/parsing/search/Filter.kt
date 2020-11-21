package com.dude.dms.brain.parsing.search

abstract class Filter : Query()

data class TextFilter(val op: Operator, val value: StringLiteral) : Filter() {
    override fun translate() = "text " + op.translate() + value.translate()
}

data class TagFilter(val op: Operator, val value: Value) : Filter() {
    override fun translate() = "tag " + op.translate() + value.translate()
}