package com.dude.dms.brain.parsing

import com.dude.dms.backend.containers.PageContainer

interface TextStripper {

    fun getPages(guid: String, language: String): Set<PageContainer>

    fun String.isValidWord() = !isNullOrBlank() &&
            any { it.isLetter() || it.isDigit() } &&
            (length < 10 || groupBy { it }.size > 2) &&
            (length > 1 || get(0).isDigit()) || this in listOf("€", "$", "%")
}
