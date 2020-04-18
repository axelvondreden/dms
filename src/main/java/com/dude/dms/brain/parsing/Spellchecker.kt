package com.dude.dms.brain.parsing

import org.languagetool.JLanguageTool
import org.languagetool.Language
import org.languagetool.language.BritishEnglish
import org.languagetool.language.GermanyGerman

class Spellchecker(language: String) {

    private val spellings = mapOf<String, Language>(
            "eng" to BritishEnglish(),
            "deu" to GermanyGerman()
    )

    private val langTool = JLanguageTool(spellings[language])

    fun check(text: String) = langTool.check(text).firstOrNull()
}