package com.dude.dms.backend.data.mails

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.Diffable
import com.dude.dms.backend.data.Tag
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToMany

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class MailFilter(
        var folder: String,

        @ManyToMany(fetch = FetchType.EAGER)
        var tags: MutableSet<Tag> = HashSet()
) : DataEntity(), Diffable<MailFilter> {

    override fun toString() = "MailFilter($folder)"
}