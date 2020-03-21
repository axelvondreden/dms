package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.data.docs.Word
import com.dude.dms.backend.service.WordService
import com.dude.dms.brain.t
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.radiobutton.RadioGroupVariant
import com.vaadin.flow.component.textfield.TextField

class WordEditDialog(
        private val wordService: WordService,
        private val word: Word,
        private val doc: Doc? = null,
        private val lines: Set<Line> = emptySet()
) : Dialog() {

    private val originalText = word.text

    private val text = TextField("Text", originalText, "").apply { setWidthFull() }

    private val group = RadioButtonGroup<String>().apply {
        addThemeVariants(RadioGroupVariant.LUMO_VERTICAL)
        val items = listOf(
                "change this",
                "change all in document (${doc?.let { wordService.countByTextAndDoc(originalText, it) } ?: lines.sumBy { line -> line.words.filter { it.text == originalText }.size }})"
        ).apply {
            if (doc != null) add("change all (${wordService.countByext(originalText)})")
        }
        setItems(items)
        value = "change this"
    }

    init {
        width = "40vw"

        val createButton = Button(t("save"), VaadinIcon.DISC.create()) { save() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }
        val cancelButton = Button(t("close"), VaadinIcon.CLOSE.create()) { close() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_ERROR)
        }
        add(text, group, HorizontalLayout(createButton, cancelButton).apply { setWidthFull() })
    }

    private fun save() {
        if (text.isEmpty) return
        val newText = text.value
        word.text = newText
        if (doc != null) wordService.save(word)
        var words = emptySet<Word>()
        if (group.value.startsWith("change all in doc")) {
            words = doc?.let { wordService.findByTextAndDoc(originalText, it) } ?: lines.flatMap { it.words }.filter { it.text == originalText }.toSet()
        } else if (group.value.startsWith("change all (")) {
            words = wordService.findByText(originalText)
        }
        words.forEach {
            it.text = newText
            if (doc != null) wordService.save(it)
        }
        close()
    }
}