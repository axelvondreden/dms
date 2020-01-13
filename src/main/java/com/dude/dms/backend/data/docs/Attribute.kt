package com.dude.dms.backend.data.docs

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.Tag
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.util.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToMany
import javax.persistence.OneToMany
import javax.validation.constraints.NotBlank

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class Attribute(
        var name: @NotBlank String,
        var isRequired: Boolean,
        var type: Type,
        @OneToMany(mappedBy = "attribute") var attributeValues: Set<AttributeValue> = HashSet(),
        @ManyToMany(mappedBy = "attributes", fetch = FetchType.EAGER) var tags: Set<Tag> = HashSet()
) : DataEntity() {

    enum class Type {
        STRING, INT, FLOAT, DATE
    }

    override fun toString() = "Attribute{name='$name', required=$isRequired}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (!super.equals(other)) return false
        return name == (other as Attribute).name
    }

    override fun hashCode() = Objects.hash(super.hashCode(), name)
}