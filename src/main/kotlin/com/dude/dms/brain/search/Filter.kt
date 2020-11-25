package com.dude.dms.brain.search

abstract class Filter : Query()

data class TextFilter(val op: Operator, val value: StringLiteral) : Filter() {
    override fun translate() = "doc.docText.text ${op.translate()} ${value.translateLike()}"
}

data class DateFilter(val op: Operator, val value: DateLiteral) : Filter() {
    override fun translate() = "doc.documentDate ${op.translate()} ${value.translate()}"
}

data class CreatedFilter(val op: Operator, val value: DateLiteral) : Filter() {
    override fun translate() = "doc.insertTime ${op.translate()} ${value.translate()}"
}

data class TagFilter(val op: Operator, val value: Value) : Filter() {
    override fun translate() = "tag ${op.translate()} ${value.translate()}"
}