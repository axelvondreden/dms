package com.dude.dms.backend.data.docs

import com.dude.dms.backend.data.DataEntity
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.validation.constraints.Size

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class TextBlock(
        @ManyToOne var doc: Doc?,

        @Column(columnDefinition = "LONGVARCHAR")
        @Size(max = 9999)
        var text: String,

        var x: Float,
        var y: Float,
        var width: Float,
        var height: Float,
        var fontSize: Float,
        var pageWidth: Float,
        var pagHeight: Float
) : DataEntity() {

    override fun toString() = "TextBlock{text='$text', x=$x, y=$y, width=$width, height=$height}"
}