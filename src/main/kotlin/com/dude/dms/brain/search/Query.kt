package com.dude.dms.brain.search

import com.dude.dms.brain.search.hint.Hint
import com.dude.dms.brain.search.hint.Hints
import com.dude.dms.brain.t

abstract class Query : Translatable, Hints {
    override val hints get() = listOf(Hint(t("and")), Hint(t("or")))
}

data class And(val left: Query, val right: Query) : Query() {
    override fun translate() = "(" + left.translate() + " and " + right.translate() + ")"
}

data class Or(val left: Query, val right: Query) : Query() {
    override fun translate() = "(" + left.translate() + " or " + right.translate() + ")"
}