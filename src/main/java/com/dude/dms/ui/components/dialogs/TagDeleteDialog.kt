package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.*
import com.dude.dms.brain.t
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
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
) : DmsDialog(t("tag.delete"), "20vw") {

    private val tagCheck = Checkbox(t("tag"), true).apply { isEnabled = false }

    private val docCheck = Checkbox("${t("docs")} (${docService.countByTag(tag)})")

    private val mailCheck = Checkbox("${t("mails")} (${mailService.countByTag(tag)})")

    private val attributeCheck = Checkbox("${t("attributes")} (${tag.attributes.size}")

    private val plainTextRuleCheck = Checkbox("${t("rules.plain")} (${plainTextRuleService.countByTag(tag)}")

    private val regexRuleCheck = Checkbox("${t("rules.regex")} (${regexRuleService.countByTag(tag)}")

    private val mailFilterCheck = Checkbox("Mail Filter (${mailFilterService.countByTag(tag)}")

    init {
        val deleteButton = Button(t("delete"), VaadinIcon.TRASH.create()) { delete() }.apply {
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
            tag.attributes.forEach(attributeService::delete)
            tag.attributes = emptySet()
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
        tagService.delete(tagService.save(tag))
        close()
    }
}