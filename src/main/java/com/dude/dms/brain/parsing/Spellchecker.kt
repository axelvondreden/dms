package com.dude.dms.brain.parsing

import org.languagetool.JLanguageTool
import org.languagetool.Language
import org.languagetool.language.BritishEnglish
import org.languagetool.language.GermanyGerman
import org.languagetool.rules.spelling.SpellingCheckRule

class Spellchecker(language: String) {

    private val spellings = mapOf<String, Language>(
            "eng" to BritishEnglish(),
            "deu" to GermanyGerman()
    )

    private val langTool = JLanguageTool(spellings[language]/*, null, UserConfig()*/).apply {
        allActiveRules.filterIsInstance<SpellingCheckRule>().forEach {
            it.setConvertsCase(true)
        }
    }

    fun check(text: String) = langTool.check(text).firstOrNull()
}