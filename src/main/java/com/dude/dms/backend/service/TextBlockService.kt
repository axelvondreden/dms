package com.dude.dms.backend.service

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.TextBlock
import com.dude.dms.backend.repositories.TextBlockRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TextBlockService @Autowired constructor(private val textBlockRepository: TextBlockRepository) : CrudService<TextBlock>(textBlockRepository) {

    fun findByDoc(doc: Doc) = textBlockRepository.findByDoc(doc)

    fun countByTextAndDoc(text: String, doc: Doc) = textBlockRepository.countByTextAndDoc(text, doc)

    fun countByext(text: String) = textBlockRepository.countByText(text)

    fun findByTextAndDoc(text: String, doc: Doc) = textBlockRepository.findByTextAndDoc(text, doc)

    fun findByText(text: String) = textBlockRepository.findByText(text)
}