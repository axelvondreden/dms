package com.dude.dms.backend.data

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.filter.TagFilter
import com.dude.dms.brain.t
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.util.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToMany
import javax.persistence.OneToOne

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class Tag(
        var name: String,
        var color: String,
        @ManyToMany(mappedBy = "tags") var docs: Set<Doc> = HashSet(),
        @ManyToMany(fetch = FetchType.EAGER) var attributes: Set<Attribute> = HashSet(),
        @OneToOne(mappedBy = "tag", fetch = FetchType.EAGER) var tagFilter: TagFilter? = null
) : DataEntity(), LogsEvents {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (!super.equals(other)) return false
        return name == (other as Tag).name
    }

    override fun hashCode() = Objects.hash(super.hashCode(), name)

    override fun toString(): String = t("tag")
}
