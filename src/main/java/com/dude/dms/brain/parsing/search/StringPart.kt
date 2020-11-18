package com.dude.dms.brain.parsing.search

class StringPart(private val text: String) : FilterPart() {

    override fun get() = "'$text'"
}