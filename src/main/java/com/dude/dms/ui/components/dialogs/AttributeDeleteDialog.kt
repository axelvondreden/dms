package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.t
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class AttributeDeleteDialog(
        private val attribute: Attribute,
        private val attributeService: AttributeService,
        private val docService: DocService,
        private val tagService: TagService
) : Dialog() {

    private val attributeCheck = Checkbox(t("attribute"), true).apply { isEnabled = false }

    private val tagCheck = Checkbox("${t("tags")} (${tagService.countByAttribute(attribute)}")

    private val docCheck = Checkbox("${t("docs")} (${docService.countByAttribute(attribute)})")

    init {
        width = "20vw"
        val deleteButton = Button(t("delete"), VaadinIcon.TRASH.create()) { delete() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_ERROR)
        }
        val wrapper = VerticalLayout(attributeCheck, tagCheck, docCheck, deleteButton).apply {
            setSizeFull()
            isPadding = false
            isSpacing = false
        }
        add(wrapper)
    }

    private fun delete() {
        if (docCheck.value) {
            docService.findByAttribute(attribute).forEach(docService::delete)
        }
        if (tagCheck.value) {
            tagService.findByAttribute(attribute).forEach(tagService::delete)
        }
        attributeService.delete(attribute)
        close()
    }
}