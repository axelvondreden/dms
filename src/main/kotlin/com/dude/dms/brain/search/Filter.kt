package com.dude.dms.brain.search

abstract class Filter : Query()

data class TextFilter(val op: Operator, val value: StringLikeLiteral) : Filter() {
    override fun translate() = "doc.docText.text ${op.translate()} ${value.translate()}"
}

data class DateFilter(val op: Operator, val value: DateLiteral) : Filter() {
    override fun translate() = "doc.documentDate ${op.translate()} ${value.translate()}"
}

data class CreatedFilter(val op: Operator, val value: DateLiteral) : Filter() {
    override fun translate() = "cast(doc.insertTime as LocalDate) ${op.translate()} ${value.translate()}"
}

data class TagFilter(val op: Operator, val value: Value) : Filter() {
    override fun translate() = "tag.name ${op.translate()} ${value.translate()}"
}

data class StringAttributeFilter(val name: String, val op: Operator, val value: Value) : Filter() {
    override fun translate() =
            "(av.attribute.name = '$name' and av.stringValue ${op.translate()} ${value.translate()})"
}
