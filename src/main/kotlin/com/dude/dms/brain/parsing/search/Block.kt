package com.dude.dms.brain.parsing.search

class Block(private val text: String) : Part {

    override fun get() = "($text)"
}