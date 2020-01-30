package com.dude.dms.backend.data.docs

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.Diffable
import com.dude.dms.backend.data.Historical
import com.dude.dms.backend.data.Tag
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

        @ManyToMany(fetch = FetchType.EAGER)
        var tags: MutableSet<Tag> = HashSet(),

        @OneToMany(mappedBy = "doc")
        var textBlocks: Set<TextBlock> = HashSet(),

        @OneToMany(mappedBy = "doc", fetch = FetchType.EAGER)
        var attributeValues: Set<AttributeValue> = HashSet(),

        @ManyToOne
        var mail: Mail? = null,

        @OneToMany(mappedBy = "doc")
        override var history: List<DocHistory> = ArrayList()
) : DataEntity(), Diffable<Doc>, Historical<DocHistory> {

    override fun toString() = "Doc{guid='$guid'}"
}