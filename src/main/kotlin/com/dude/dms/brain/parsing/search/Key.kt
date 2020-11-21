package com.dude.dms.brain.parsing.search

abstract class Key : Hints

object TextKey : Key() {
    override val hints get() = listOf(Hint("~=", "Like"), Hint("!~=", "Not Like"))
}
object TagKey: Key() {
    override val hints get() = listOf(
            Hint("=", "Equals"),
            Hint("!=", "Not Equals"),
            Hint("in", "In Array"),
            Hint("!in", "Not In Array")
    )
}