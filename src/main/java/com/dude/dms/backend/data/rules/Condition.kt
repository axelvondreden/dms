package com.dude.dms.backend.data.rules

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.WordContainer
import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.docs.Attribute
import java.util.*
import javax.persistence.*
import kotlin.collections.ArrayList

@Entity
class Condition(
        @OneToOne var attribute: Attribute? = null,
        @ManyToOne var parent: Condition? = null,
        @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER) var children: MutableList<Condition> = ArrayList(),
        @Enumerated(EnumType.STRING) var type: ConditionType,
        var text: String? = null
) : DataEntity() {

    @Transient
    private val hash = UUID.randomUUID().toString()

    fun validate(doc: DocContainer, word: WordContainer): Boolean {
        return when (type) {
            ConditionType.AND -> children.all { it.validate(doc, word) }
            ConditionType.OR -> children.any { it.validate(doc, word) }
            ConditionType.EQUALS -> word.text == text
            ConditionType.CONTAINS -> text!! in word.text
            ConditionType.PREVIOUS_EQUALS -> {
                val words = doc.getLine(word).words.sortedBy { it.x }
                val index = words.indexOf(word.word)
                index > 0 && words[index - 1].text == text
            }
            ConditionType.PREVIOUS_CONTAINS -> {
                val words = doc.getLine(word).words.sortedBy { it.x }
                val index = words.indexOf(word.word)
                index > 0 && text!! in words[index - 1].text!!
            }
            ConditionType.NEXT_EQUALS -> {
                val words = doc.getLine(word).words.sortedBy { it.x }
                val index = words.indexOf(word.word)
                index < words.size - 1 && words[index + 1].text == text
            }
            ConditionType.NEXT_CONTAINS -> {
                val words = doc.getLine(word).words.sortedBy { it.x }
                val index = words.indexOf(word.word)
                index < words.size - 1 && text!! in words[index + 1].text!!
            }
            ConditionType.LINE_CONTAINS_WORD -> doc.getLine(word).words.any { it.text == text }
            ConditionType.LINE_CONTAINS_TEXT -> doc.getLine(word).getFullText().contains(text!!)
        }
    }

    fun isValid(): Boolean {
        return when (type) {
            ConditionType.AND -> children.size > 1 && children.all { it.isValid() }
            ConditionType.OR -> children.size > 1 && children.all { it.isValid() }
            else -> !text.isNullOrBlank()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Condition
        return hash == other.hash
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + hash.hashCode()
        return result
    }
}
