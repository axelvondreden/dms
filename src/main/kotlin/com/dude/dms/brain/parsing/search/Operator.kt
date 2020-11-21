package com.dude.dms.brain.parsing.search

abstract class Operator : Translatable, Hints

object Equal : Operator() {
    override fun translate() = " = "
    override val hints get() = listOf(
            Hint("\"\"", "String", 1),
            Hint("0.0", "Number")
    )
}

object NotEqual : Operator() {
    override fun translate() = " != "
    override val hints get() = listOf(
            Hint("\"\"", "String", 1),
            Hint("0.0", "Number")
    )
}

object Less : Operator() {
    override fun translate() = " <"
    override val hints get() = listOf(Hint("0.0", "Number"))
}

object Greater : Operator() {
    override fun translate() = " >"
    override val hints get() = listOf(Hint("0.0", "Number"))
}

object InArray : Operator() {
    override fun translate() = " IN "
    override val hints get() = listOf(Hint("[\"\", \"\"]", "Array", 6))
}

object NotInArray : Operator() {
    override fun translate() = " NOT IN "
    override val hints get() = listOf(Hint("[\"\", \"\"]", "Array", 6))
}

object Like : Operator() {
    override fun translate() = " LIKE "
    override val hints get() = listOf(Hint("\"\"", "String", 1))
}

object NotLike : Operator() {
    override fun translate() = " NOT LIKE "
    override val hints get() = listOf(Hint("\"\"", "String", 1))
}