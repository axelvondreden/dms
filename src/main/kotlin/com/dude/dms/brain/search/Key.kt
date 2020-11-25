package com.dude.dms.brain.search

import com.dude.dms.brain.search.hint.Hint
import com.dude.dms.brain.search.hint.Hints
import com.dude.dms.brain.t
import com.dude.dms.utils.convert
import com.dude.dms.utils.tagService
import java.time.LocalDate

abstract class Key : Hints {
    abstract fun getValueHints(op: Operator): List<Hint>
}

abstract class OrderKey : Key(), Translatable {
    abstract val orderHints: List<Hint>
}

object TextKey : Key() {
    override val hints get() = listOf(Hint("~=", t("search.like")), Hint("!~=", t("search.notlike")))

    override fun getValueHints(op: Operator) = when (op) {
        is Like, is NotLike -> listOf(Hint("\"\"", t("text"), 1))
        else -> emptyList()
    }
}

object DateKey : OrderKey() {
    override val hints get() = listOf(
            Hint("=", t("search.equal")),
            Hint("!=", t("search.notequal")),
            Hint("<", t("search.less")),
            Hint(">", t("search.greater"))
    )

    override val orderHints get() = listOf(
            Hint(t("search.order.asc"), t("ascending")),
            Hint(t("search.order.desc"), t("descending"))
    )

    override fun translate() = "doc.documentDate"

    override fun getValueHints(op: Operator) = when (op) {
        is Equal, is NotEqual, is Less, is Greater -> listOf(Hint(LocalDate.now().convert(), t("date")))
        else -> emptyList()
    }
}

object CreatedKey : OrderKey() {
    override val hints get() = listOf(
            Hint("=", t("search.equal")),
            Hint("!=", t("search.notequal")),
            Hint("<", t("search.less")),
            Hint(">", t("search.greater"))
    )

    override val orderHints get() = listOf(
            Hint(t("search.order.asc"), t("ascending")),
            Hint(t("search.order.desc"), t("descending"))
    )

    override fun translate() = "doc.insertTime"

    override fun getValueHints(op: Operator) = when (op) {
        is Equal, is NotEqual, is Less, is Greater -> listOf(Hint(LocalDate.now().convert(), t("date")))
        else -> emptyList()
    }
}

object TagKey: Key() {
    override val hints get() = listOf(
            Hint("=", t("search.equal")),
            Hint("!=", t("search.notequal")),
            Hint("in", t("search.inarray")),
            Hint("!in", t("search.notinarray"))
    )

    override fun getValueHints(op: Operator) = when (op) {
        is Equal, is NotEqual -> tagService.findAll().map { Hint(it.name, t("tag")) }
        is InArray, NotInArray -> listOf(Hint("[, ]", t("list"), 3))
        else -> emptyList()
    }
}

data class StringAttributeKey(val name: String): Key() {
    override val hints get() = listOf(
            Hint("=", t("search.equal")),
            Hint("!=", t("search.notequal")),
            Hint("~=", t("search.like")),
            Hint("!~=", t("search.notlike")),
            Hint("in", t("search.inarray")),
            Hint("!in", t("search.notinarray"))
    )

    override fun getValueHints(op: Operator) = when (op) {
        is Equal, is NotEqual, is Like, is NotLike -> listOf(Hint("\"\"", t("text"), 1))
        is InArray, NotInArray -> listOf(Hint("[\"\", \"\"]", t("list"), 6))
        else -> emptyList()
    }
}