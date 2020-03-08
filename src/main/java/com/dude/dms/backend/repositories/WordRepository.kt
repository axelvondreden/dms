package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.data.docs.Word
import org.springframework.data.jpa.repository.JpaRepository

interface WordRepository : JpaRepository<Word, Long> {

    fun findByLine(line: Line): Set<Word>

    fun countByLine(line: Line): Long

    fun findByLine_Doc(doc: Doc): Set<Word>

    fun countByTextAndLine_Doc(text: String, doc: Doc): Long

    fun countByText(text: String): Long

    fun findByTextAndLine_Doc(text: String, doc: Doc): Set<Word>

    fun findByText(text: String): Set<Word>
}