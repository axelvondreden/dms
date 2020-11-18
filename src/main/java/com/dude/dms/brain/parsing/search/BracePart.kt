package com.dude.dms.brain.parsing.search

class BracePart(private val text: String) : LogicPart() {

    override fun get() = "($text)"
}