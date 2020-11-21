package com.dude.dms.brain.parsing.search

abstract class Query : Translatable, Hints {
    override val hints get() = listOf(Hint("and"), Hint("or"))
}

data class And(val left: Query, val right: Query) : Query() {
    override fun translate() = "(" + left.translate() + " and " + right.translate() + ")"
}

data class Or(val left: Query, val right: Query) : Query() {
    override fun translate() = "(" + left.translate() + " or " + right.translate() + ")"
}