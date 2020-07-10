package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.WordContainer
import com.dude.dms.brain.t
import com.dude.dms.extensions.round
import com.dude.dms.extensions.wordService
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextField
import org.languagetool.rules.SuggestedReplacement

class WordEditDialog(private val wordContainer: WordContainer) : DmsDialog(t("word.edit"), 40) {

    private val originalText = wordContainer.word.text

    private var text: TextField

    init {
        text = textField("Text") {
            setWidthFull()
            value = originalText
        }
        if (wordContainer.spelling != null) {
            grid<SuggestedReplacement> {
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
        }
        horizontalLayout {
            setWidthFull()

            button(t("save"), VaadinIcon.DISC.create()) {
                onLeftClick { save() }
                setWidthFull()
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            }
            button(t("close"), VaadinIcon.CLOSE.create()) {
                onLeftClick { close() }
                setWidthFull()
                addThemeVariants(ButtonVariant.LUMO_ERROR)
            }
        }
    }

    private fun save() {
        if (text.isEmpty) return
        val newText = text.value
        wordContainer.word.text = newText
        if (wordContainer.word.id > 0) wordService.save(wordContainer.word)
        close()
    }
}