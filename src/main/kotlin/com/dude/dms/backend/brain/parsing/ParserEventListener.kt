package com.dude.dms.backend.brain.parsing

interface ParserEventListener {
    fun onParse(success: Boolean)
}