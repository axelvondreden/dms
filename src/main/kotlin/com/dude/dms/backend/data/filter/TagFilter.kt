package com.dude.dms.backend.data.filter

import com.dude.dms.backend.data.Tag
import javax.persistence.Entity
import javax.persistence.OneToOne

@Entity
class TagFilter(
    @OneToOne var tag: Tag,
    var filter: String
) : Filter() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as TagFilter
        if (tag != other.tag) return false
        if (filter != other.filter) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + tag.hashCode()
        result = 31 * result + filter.hashCode()
        return result
    }
}
