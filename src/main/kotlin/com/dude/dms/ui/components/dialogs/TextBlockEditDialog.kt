package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.docs.TextBlock
import com.dude.dms.backend.service.TextBlockService
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.radiobutton.RadioGroupVariant
import com.vaadin.flow.component.textfield.TextField

class TextBlockEditDialog(private val textBlock: TextBlock, private val textBlockService: TextBlockService) : EventDialog<TextBlock>() {

    private val originalText = textBlock.text

    private val text = TextField("Text", originalText, "").apply { setWidthFull() }

    private val group = RadioButtonGroup<String>().apply {
        addThemeVariants(RadioGroupVariant.LUMO_VERTICAL)
        setItems(
                "change this",
                "change all in document (${textBlockService.countByTextAndDoc(originalText, textBlock.doc)})",
                "change all (${textBlockService.countByext(originalText)})"
        )
        value = "change this"
    }

    init {
        width = "40vw"

        val createButton = Button("Save", VaadinIcon.DISC.create()) { save() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }
        val cancelButton = Button("Close", VaadinIcon.CLOSE.create()) { close() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_ERROR)
        }
        add(text, group, HorizontalLayout(createButton, cancelButton).apply { setWidthFull() })
    }

    private fun save() {
        if (text.isEmpty) {
            text.errorMessage = "Text can not be empty!"
            return
        }
        val newText = text.value
        textBlock.text = newText
        textBlockService.save(textBlock)
        var textBlocks = emptyList<TextBlock>()
        if (group.value.startsWith("change all in doc")) {
            textBlocks = textBlockService.findByTextAndDoc(originalText, textBlock.doc)
        } else if (group.value.startsWith("change all (")) {
            textBlocks = textBlockService.findByText(originalText)
        }
        textBlocks.forEach {
            it.text = newText
            textBlockService.save(it)
        }
        triggerEditEvent(textBlock)
        close()
    }
}