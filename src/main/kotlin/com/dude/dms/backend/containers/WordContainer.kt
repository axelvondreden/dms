package com.dude.dms.backend.containers

import com.dude.dms.backend.data.docs.Word
import org.languagetool.rules.RuleMatch

data class WordContainer(var word: Word, var spelling: RuleMatch? = null) {

    val text: String
        get() = word.text!!

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as WordContainer
        if (word != other.word) return false
        return true
    }

    override fun hashCode(): Int {
        return word.hashCode()
    }
}
