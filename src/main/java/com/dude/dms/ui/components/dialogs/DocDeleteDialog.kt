package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.MailService
import com.dude.dms.brain.t
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.checkBox
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.icon.VaadinIcon

class DocDeleteDialog(
        private val docService: DocService,
        private val mailService: MailService,
        private val docContainer: DocContainer
) : DmsDialog(t("doc.delete"), 20) {

    private lateinit var docCheck: Checkbox

    private lateinit var mailCheck: Checkbox

    init {
        verticalLayout(isPadding = false, isSpacing = false) {
            setSizeFull()

            docCheck = checkBox(t("doc")) {
                value = true
                isEnabled = false
            }
            mailCheck = checkBox("${t("mail")} (${docContainer.doc?.let { mailService.countByDoc(it) }})")
            button(t("delete"), VaadinIcon.TRASH.create()) {
                onLeftClick { delete() }
                setWidthFull()
                addThemeVariants(ButtonVariant.LUMO_ERROR)
            }
        }
    }

    private fun delete() {
        docContainer.doc?.let {
            if (mailCheck.value) {
                mailService.findByDoc(it).forEach(mailService::delete)
            }
            docService.findByGuid(it.guid)?.let { doc -> docService.softDelete(doc) }
        }
        close()
    }
}