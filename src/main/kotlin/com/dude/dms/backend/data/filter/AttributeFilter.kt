package com.dude.dms.backend.data.filter

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.LogsEvents
import com.dude.dms.backend.data.docs.Attribute
import javax.persistence.Entity
import javax.persistence.OneToOne

@Entity
class AttributeFilter(
    @OneToOne var attribute: Attribute,
    var filter: String
) : DataEntity(), LogsEvents {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as AttributeFilter
        if (attribute != other.attribute) return false
        if (filter != other.filter) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + attribute.hashCode()
        result = 31 * result + filter.hashCode()
        return result
    }
}
