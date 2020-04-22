package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.WordContainer
import com.dude.dms.backend.service.WordService
import com.dude.dms.brain.t
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import org.languagetool.rules.SuggestedReplacement
import kotlin.math.round

class WordEditDialog(
        private val wordService: WordService,
        private val wordContainer: WordContainer
) : Dialog() {

    private val originalText = wordContainer.word.text

    private val text = TextField("Text", originalText, "").apply { setWidthFull() }

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
        add(text)
        if (wordContainer.spelling != null) {
            val grid = Grid<SuggestedReplacement>().apply {
                setWidthFull()
                maxHeight = "200px"
                val items = wordContainer.spelling!!.suggestedReplacementObjects
                setItems(items)
                addColumn { it.replacement }.setHeader(t("correction"))
                if (items.any { it.confidence != null }) {
                    addColumn { "${it.confidence?.round(2) ?: 0} %" }.setHeader(t("confidence"))
                }
                addItemClickListener { text.value = it.item.replacement }
            }
            add(grid)
        }
        add(HorizontalLayout(createButton, cancelButton).apply { setWidthFull() })
    }

    private fun save() {
        if (text.isEmpty) return
        val newText = text.value
        wordContainer.word.text = newText
        if (wordContainer.word.id > 0) wordService.save(wordContainer.word)
        close()
    }

    fun Float.round(decimals: Int): Float {
        var multiplier = 1.0F
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }
}