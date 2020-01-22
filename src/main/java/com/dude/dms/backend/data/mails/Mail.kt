package com.dude.dms.backend.data.mails

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
class Mail(
        var guid: String,

        var documentDate: LocalDate? = null,

        @Column(columnDefinition = "LONGVARCHAR")
        @Size(max = 99999)
        var rawText: String? = null,

        @ManyToMany(fetch = FetchType.EAGER)
        var tags: MutableSet<Tag> = HashSet(),

        @OneToMany(mappedBy = "doc")
        var textBlocks: Set<TextBlock> = HashSet(),

        @OneToMany(mappedBy = "doc", fetch = FetchType.EAGER)
        var attributeValues: Set<AttributeValue> = HashSet(),

        @OneToMany(mappedBy = "doc")
        @OrderBy("timestamp")
        override var history: List<DocHistory> = ArrayList()
) : DataEntity(), Diffable<Mail>, Historical<DocHistory> {

    override fun toString() = "Doc{guid='$guid'}"
}