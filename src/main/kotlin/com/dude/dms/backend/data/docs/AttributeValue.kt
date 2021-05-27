package com.dude.dms.backend.data.docs

import com.dude.dms.backend.data.LogsEvents
import com.dude.dms.backend.data.RestorableEntity
import com.dude.dms.brain.t
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.time.LocalDate
import java.util.*
import javax.persistence.Entity
import javax.persistence.ManyToOne

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class AttributeValue(
        @ManyToOne var doc: Doc?,
        @ManyToOne var attribute: Attribute,
        var stringValue: String? = null,
        var intValue: Int? = null,
        var floatValue: Float? = null,
        var dateValue: LocalDate? = null
) : RestorableEntity(), LogsEvents {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        return doc == (other as AttributeValue).doc && attribute == other.attribute
    }

    override fun hashCode(): Int = Objects.hash(super.hashCode(), doc, attribute)

    override fun toString(): String = t("attributevalue")

    val convertedValue: String
        get() = when (attribute.type) {
            Attribute.Type.STRING -> stringValue.toString()
            Attribute.Type.INT -> intValue.toString()
            Attribute.Type.FLOAT -> floatValue.toString()
            Attribute.Type.DATE -> dateValue.toString()
        }
}
