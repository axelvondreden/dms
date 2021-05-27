package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.WordContainer
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.t
import com.dude.dms.utils.round
import com.dude.dms.utils.wordService
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import org.languagetool.rules.SuggestedReplacement

class WordEditDialog(private val wordContainer: WordContainer) : DmsDialog(t("word.edit"), 40) {

    private val originalText = wordContainer.word.text

    init {
        val text = textField("Text") {
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
                setWidthFull()
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                onLeftClick {
                    if (text.isEmpty) {
                        LOGGER.showError(t("text.missing"), UI.getCurrent())
                    } else {
                        wordContainer.word.text = text.value
                        if (wordContainer.word.id > 0) wordService.save(wordContainer.word)
                        close()
                    }
                }
            }
            button(t("close"), VaadinIcon.CLOSE.create()) {
                setWidthFull()
                addThemeVariants(ButtonVariant.LUMO_ERROR)
                onLeftClick { close() }
            }
        }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(WordEditDialog::class.java)
    }
}
