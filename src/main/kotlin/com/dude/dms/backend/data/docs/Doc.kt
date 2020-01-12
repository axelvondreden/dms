package com.dude.dms.backend.data.docs

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.Diffable
import com.dude.dms.backend.data.Historical
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.history.DocHistory
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
        var rawText: @Size(max = 99999999) String? = null,
        @ManyToMany(fetch = FetchType.EAGER) var tags: Set<Tag>,
        @OneToMany(mappedBy = "doc") var textBlocks: Set<TextBlock>,
        @OneToMany(mappedBy = "doc", fetch = FetchType.EAGER) var attributeValues: Set<AttributeValue>,
        @OneToMany(mappedBy = "doc") @OrderBy("timestamp") override var history: List<DocHistory>
) : DataEntity(), Diffable<Doc>, Historical<DocHistory> {

    override fun toString() = "Doc{guid='$guid'}"
}