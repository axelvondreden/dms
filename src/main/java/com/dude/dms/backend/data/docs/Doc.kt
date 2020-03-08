package com.dude.dms.backend.data.docs

import com.dude.dms.backend.data.*
import com.dude.dms.backend.data.history.DocHistory
import com.dude.dms.backend.data.mails.Mail
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.time.LocalDate
import javax.persistence.*
import javax.validation.constraints.Size

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class Doc(
        var guid: String,

        var documentDate: LocalDate? = null,

        @Column(columnDefinition = "LONGVARCHAR")
        @Size(max = 99999)
        var rawText: String? = null,

        @ManyToMany
        var tags: MutableSet<Tag> = HashSet(),

        @OneToMany(mappedBy = "doc")
        var lines: Set<Line> = HashSet(),

        @OneToMany(mappedBy = "doc")
        var attributeValues: Set<AttributeValue> = HashSet(),

        @ManyToOne
        var mail: Mail? = null,

        @OneToMany(mappedBy = "doc")
        override var history: Set<DocHistory> = HashSet()
) : DataEntity(), Diffable<Doc>, Historical<DocHistory>, LogsEvents {

    override fun showEvents() = true

    override fun toString() = "Doc($guid)"
}