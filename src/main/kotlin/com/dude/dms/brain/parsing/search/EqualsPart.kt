package com.dude.dms.brain.parsing.search

class EqualsPart(private val keyPart: KeyPart, private val filterPart: FilterPart): OpPart(keyPart, filterPart) {

    override fun get() = "(${keyPart.get()} = ${filterPart.get()})"
}