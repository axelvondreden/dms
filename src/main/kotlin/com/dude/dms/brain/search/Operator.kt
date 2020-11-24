package com.dude.dms.brain.search

import com.dude.dms.brain.search.hint.Hint
import com.dude.dms.brain.search.hint.Hints
import com.dude.dms.brain.t

abstract class Operator : Translatable, Hints

object Equal : Operator() {
    override fun translate() = " = "
    override val hints get() = listOf(
            Hint("\"\"", t("text"), 1),
            Hint("0.0", t("number"))
    )
}

object NotEqual : Operator() {
    override fun translate() = " != "
    override val hints get() = listOf(
            Hint("\"\"", t("text"), 1),
            Hint("0.0", t("number"))
    )
}

object Less : Operator() {
    override fun translate() = " <"
    override val hints get() = listOf(Hint("0.0", t("number")))
}

object Greater : Operator() {
    override fun translate() = " >"
    override val hints get() = listOf(Hint("0.0", t("number")))
}

object InArray : Operator() {
    override fun translate() = " IN "
    override val hints get() = listOf(Hint("[\"\", \"\"]", t("list"), 6))
}

object NotInArray : Operator() {
    override fun translate() = " NOT IN "
    override val hints get() = listOf(Hint("[\"\", \"\"]", t("list"), 6))
}

object Like : Operator() {
    override fun translate() = " LIKE "
    override val hints get() = listOf(Hint("\"\"", t("text"), 1))
}

object NotLike : Operator() {
    override fun translate() = " NOT LIKE "
    override val hints get() = listOf(Hint("\"\"", t("text"), 1))
}