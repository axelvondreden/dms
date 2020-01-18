package com.dude.dms.brain.parsing

import java.io.File

interface Parser {
    fun parse(file: File)
}