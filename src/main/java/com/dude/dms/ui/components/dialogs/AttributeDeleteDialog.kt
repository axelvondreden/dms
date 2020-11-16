package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.brain.t
import com.dude.dms.extensions.attributeService
import com.dude.dms.extensions.docService
import com.dude.dms.extensions.tagService
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.checkBox
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.icon.VaadinIcon

class AttributeDeleteDialog(private val attribute: Attribute) : DmsDialog(t("attribute.delete"), 20) {

    private lateinit var attributeCheck: Checkbox

    private lateinit var tagCheck: Checkbox

    private lateinit var docCheck: Checkbox

    init {
        verticalLayout(isPadding = false, isSpacing = false) {
            setSizeFull()

            attributeCheck = checkBox(t("attribute")) {
                value = true
                isEnabled = false
            }
            tagCheck = checkBox("${t("tags")} (${tagService.countByAttribute(attribute)}")
            docCheck = checkBox("${t("docs")} (${docService.countByAttribute(attribute)})")
            button(t("delete"), VaadinIcon.TRASH.create()) {
                onLeftClick { delete() }
                setWidthFull()
                addThemeVariants(ButtonVariant.LUMO_ERROR)
            }
        }
    }

    private fun delete() {
        if (docCheck.value) {
            docService.findByAttribute(attribute).forEach(docService::softDelete)
        }
        if (tagCheck.value) {
            tagService.findByAttribute(attribute).forEach(tagService::delete)
        }
        attributeService.delete(attribute)
        close()
    }
}