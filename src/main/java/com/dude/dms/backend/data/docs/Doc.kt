package com.dude.dms.backend.data.docs

import com.dude.dms.backend.data.*
import com.dude.dms.backend.data.mails.Mail
import com.dude.dms.brain.t
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.Size

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class Doc(
        var guid: String,

        var documentDate: LocalDate? = null,

        var insertTime: LocalDateTime? = null,

        @Column(columnDefinition = "LONGVARCHAR")
        @Size(max = 99999)
        var rawText: String? = null,

        @ManyToMany(fetch = FetchType.EAGER)
        var tags: MutableSet<Tag> = HashSet(),

        @OneToMany(mappedBy = "doc", fetch = FetchType.EAGER)
        var lines: Set<Line> = HashSet(),

        @OneToMany(mappedBy = "doc", fetch = FetchType.EAGER)
        var attributeValues: MutableSet<AttributeValue> = HashSet(),

        @ManyToOne
        var mail: Mail? = null
) : DataEntity(), Diffable<Doc>, LogsEvents {

    override fun toString() = t("doc")
}