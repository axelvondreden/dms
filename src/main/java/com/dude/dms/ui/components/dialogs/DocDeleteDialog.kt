package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.MailService
import com.dude.dms.brain.t
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class DocDeleteDialog(private val docContainer: DocContainer, private val docService: DocService, private val mailService: MailService) : Dialog() {

    private val docCheck = Checkbox(t("doc"), true).apply { isEnabled = false }

    private val mailCheck = Checkbox("${t("mail")} (${docContainer.doc?.let { mailService.countByDoc(it) }})")

    init {
        width = "20vw"
        val deleteButton = Button(t("delete"), VaadinIcon.TRASH.create()) { delete() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_ERROR)
        }
        val wrapper = VerticalLayout(docCheck, mailCheck, deleteButton).apply {
            setSizeFull()
            isPadding = false
            isSpacing = false
        }
        add(wrapper)
    }

    private fun delete() {
        docContainer.doc?.let {
            if (mailCheck.value) {
                mailService.findByDoc(it).forEach(mailService::delete)
            }
            docService.delete(it)
        }
        close()
    }
}