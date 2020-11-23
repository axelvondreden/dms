package com.dude.dms.backend.data.docs

import com.dude.dms.backend.data.Diffable
import com.dude.dms.backend.data.LogsEvents
import com.dude.dms.backend.data.RestorableEntity
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.rules.Condition
import com.dude.dms.brain.t
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.Size

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class DocText(
        @OneToOne(mappedBy = "docText", cascade = [CascadeType.MERGE]) var doc: Doc,

        @Column(columnDefinition = "LONGVARCHAR")
        @Size(max = 9999999)
        var text: String?,
) : RestorableEntity(), Diffable<DocText>, LogsEvents {

    override fun toString(): String = "${t("doc")} text"
}