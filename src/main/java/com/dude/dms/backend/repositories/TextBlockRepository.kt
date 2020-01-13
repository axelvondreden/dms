package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.TextBlock
import org.springframework.data.jpa.repository.JpaRepository

interface TextBlockRepository : JpaRepository<TextBlock, Long> {
    fun findByDoc(doc: Doc): List<TextBlock>
    fun countByTextAndDoc(text: String, doc: Doc): Long
    fun countByText(text: String): Long
    fun findByTextAndDoc(text: String, doc: Doc): List<TextBlock>
    fun findByText(text: String): List<TextBlock>
}