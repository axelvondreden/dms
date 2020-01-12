package com.dude.dms.backend.brain.parsing

import java.io.File

interface Parser {
    fun parse(file: File?)
}