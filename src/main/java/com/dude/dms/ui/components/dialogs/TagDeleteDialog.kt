package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class TagDeleteDialog(
        private val tag: Tag,
        private val tagService: TagService,
        private val docService: DocService,
        private val mailService: MailService,
        private val attributeService: AttributeService,
        private val plainTextRuleService: PlainTextRuleService,
        private val regexRuleService: RegexRuleService,
        private val mailFilterService: MailFilterService
) : Dialog() {

    private val tagCheck = Checkbox("Tag", true).apply { isEnabled = false }

    private val docCheck = Checkbox("Docs (${docService.countByTag(tag)})")

    private val mailCheck = Checkbox("Mails (${mailService.countByTag(tag)})")

    private val attributeCheck = Checkbox("Attributes (${attributeService.countByTag(tag)}")

    private val plainTextRuleCheck = Checkbox("Text Rules (${plainTextRuleService.countByTag(tag)}")

    private val regexRuleCheck = Checkbox("Regex Rules (${regexRuleService.countByTag(tag)}")

    private val mailFilterCheck = Checkbox("Mail Filters (${mailFilterService.countByTag(tag)}")

    init {
        width = "20vw"
        val deleteButton = Button("Delete", VaadinIcon.TRASH.create()) { delete() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_ERROR)
        }
        val wrapper = VerticalLayout(tagCheck, docCheck, mailCheck, attributeCheck, deleteButton).apply {
            setSizeFull()
            isPadding = false
            isSpacing = false
        }
        add(wrapper)
    }

    private fun delete() {
        if (docCheck.value) {
            docService.findByTag(tag).forEach(docService::delete)
        }
        if (mailCheck.value) {
            mailService.findByTag(tag).forEach(mailService::delete)
        }
        if (attributeCheck.value) {
            attributeService.findByTag(tag).forEach(attributeService::delete)
        }
        if (plainTextRuleCheck.value) {
            plainTextRuleService.findByTag(tag).forEach(plainTextRuleService::delete)
        }
        if (regexRuleCheck.value) {
            regexRuleService.findByTag(tag).forEach(regexRuleService::delete)
        }
        if (mailFilterCheck.value) {
            mailFilterService.findByTag(tag).forEach(mailFilterService::delete)
        }
        tagService.delete(tag)
        close()
    }
}