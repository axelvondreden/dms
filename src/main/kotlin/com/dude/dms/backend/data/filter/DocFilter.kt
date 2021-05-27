package com.dude.dms.backend.data.filter

import com.dude.dms.brain.t
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.util.*
import javax.persistence.Entity

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class DocFilter(
        var name: String,
        var filter: String
) : Filter() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (!super.equals(other)) return false
        return name == (other as DocFilter).name
    }

    override fun hashCode() = Objects.hash(super.hashCode(), name)

    override fun toString(): String = t("query")

}
