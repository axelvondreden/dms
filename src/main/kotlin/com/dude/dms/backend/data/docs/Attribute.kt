package com.dude.dms.backend.data.docs

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.LogsEvents
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.rules.Condition
import com.dude.dms.brain.t
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class Attribute(
        var name: @NotBlank String,
        var isRequired: Boolean,
        var type: Type,
        @OneToMany(mappedBy = "attribute") var attributeValues: Set<AttributeValue> = HashSet(),
        @ManyToMany(mappedBy = "attributes") var tags: Set<Tag> = HashSet(),
        @OneToOne(mappedBy = "attribute", cascade = [CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH]) var condition: Condition? = null
) : DataEntity(), LogsEvents {

    enum class Type {
        STRING, INT, FLOAT, DATE
    }

    override fun toString(): String = t("attribute")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (!super.equals(other)) return false
        return name == (other as Attribute).name
    }

    override fun hashCode() = Objects.hash(super.hashCode(), name)
}