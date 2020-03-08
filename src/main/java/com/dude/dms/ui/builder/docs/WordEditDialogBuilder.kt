package com.dude.dms.ui.builder.docs

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Word
import com.dude.dms.backend.service.WordService
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.components.dialogs.WordEditDialog

class WordEditDialogBuilder(
        private val doc: Doc,
        private val word: Word,
        private val wordService: WordService
): Builder<WordEditDialog> {

    override fun build() = WordEditDialog(doc, word, wordService)
}