package com.dude.dms.brain.parsing.search

import com.dude.dms.brain.t
import com.dude.dms.utils.convert
import java.time.LocalDate

abstract class Key : Hints {
    abstract fun getValueHints(op: Operator): List<Hint>
}

object TextKey : Key() {
    override val hints get() = listOf(Hint("~=", t("search.like")), Hint("!~=", t("search.notlike")))

    override fun getValueHints(op: Operator): List<Hint> {
        return when (op) {
            is Like, is NotLike -> listOf(Hint("\"\"", t("text"), 1))
            else -> emptyList()
        }
    }
}

object DateKey : Key() {
    override val hints get() = listOf(
            Hint("=", t("search.equal")),
            Hint("!=", t("search.notequal")),
            Hint("<", t("search.less")),
            Hint(">", t("search.greater"))
    )

    override fun getValueHints(op: Operator): List<Hint> {
        return when (op) {
            is Equal, is NotEqual, is Less, is Greater -> listOf(Hint(LocalDate.now().convert(), t("date")))
            else -> emptyList()
        }
    }
}

object TagKey: Key() {
    override val hints get() = listOf(
            Hint("=", t("search.equal")),
            Hint("!=", t("search.notequal")),
            Hint("in", t("search.inarray")),
            Hint("!in", t("search.notinarray"))
    )

    override fun getValueHints(op: Operator): List<Hint> {
        return when (op) {
            is Equal, is NotEqual -> listOf(Hint("*tags", t("tag")))
            is InArray, NotInArray -> listOf(Hint("[, ]", t("list"), 3))
            else -> emptyList()
        }
    }
}