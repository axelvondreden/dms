package com.dude.dms.backend.data.docs

import com.dude.dms.backend.data.LogsEvents
import com.dude.dms.backend.data.RestorableEntity
import com.dude.dms.brain.t
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToOne
import javax.validation.constraints.Size

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class DocText(
        @OneToOne(mappedBy = "docText") var doc: Doc,

        @Column(columnDefinition = "LONGVARCHAR")
        @Size(max = 9999999)
        var text: String?,
) : RestorableEntity(), LogsEvents {

    override fun toString(): String = "${t("doc")} text"
}