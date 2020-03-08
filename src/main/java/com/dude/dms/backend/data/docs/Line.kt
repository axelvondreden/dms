package com.dude.dms.backend.data.docs

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.LogsEvents
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class Line(
        @ManyToOne var doc: Doc?,
        @OneToMany(mappedBy = "line") var words: Set<Word> = HashSet(),
        var y: Float
) : DataEntity(), LogsEvents {

    override fun showEvents() = false

    override fun toString() = "Line"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Line

        if (id > 0 && other.id > 0) return super.equals(other)
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
}