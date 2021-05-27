package com.dude.dms.backend.data.docs

import com.dude.dms.backend.data.LogsEvents
import com.dude.dms.backend.data.RestorableEntity
import com.dude.dms.brain.t
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import javax.persistence.*

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class Page(
        @ManyToOne var doc: Doc?,
        @OneToMany(mappedBy = "page", fetch = FetchType.EAGER, cascade = [CascadeType.REMOVE])
        var lines: Set<Line> = HashSet(),
        var nr: Int
) : RestorableEntity(), LogsEvents {

    fun getFullText() = lines.sortedBy { it.y }.joinToString("\n") { it.getFullText() }

    override fun toString(): String = t("page")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Page
        if (id > 0 && other.id > 0) return super.equals(other)
        if (nr != other.nr) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + nr.hashCode()
        return result
    }
}
