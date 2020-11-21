package com.dude.dms.backend.data.docs

import com.dude.dms.backend.data.Diffable
import com.dude.dms.backend.data.LogsEvents
import com.dude.dms.backend.data.RestorableEntity
import com.dude.dms.backend.data.Tag
import com.dude.dms.brain.t
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class Doc(
        var guid: String,

        var documentDate: LocalDate? = null,

        var insertTime: LocalDateTime? = null,

        @ManyToMany(fetch = FetchType.EAGER)
        var tags: Set<Tag> = HashSet(),

        @OneToMany(mappedBy = "doc", fetch = FetchType.EAGER)
        var pages: Set<Page> = HashSet(),

        @OneToMany(mappedBy = "doc", fetch = FetchType.EAGER)
        var attributeValues: Set<AttributeValue> = HashSet()
) : RestorableEntity(), Diffable<Doc>, LogsEvents {

    fun getFullText() = pages.sortedBy { it.nr }.joinToString("\n") { it.getFullText() }

    fun getLine(word: Word) = pages.flatMap { it.lines }.first { word in it.words }

    override fun toString(): String = t("doc")
}