package com.dude.dms.brain.parsing.search

class KeyPart(private val key: String): Part {

    override fun get(): String {
        return key
    }
}