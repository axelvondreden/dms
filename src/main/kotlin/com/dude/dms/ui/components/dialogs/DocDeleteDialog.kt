package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.brain.t
import com.dude.dms.utils.docService
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.checkBox
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon

class DocDeleteDialog(private val docContainer: DocContainer) : DmsDialog(t("doc.delete"), 20) {

    init {
        verticalLayout(isPadding = false, isSpacing = false) {
            setSizeFull()

            checkBox(t("doc")) {
                value = true
                isEnabled = false
            }
            button(t("delete"), VaadinIcon.TRASH.create()) {
                setWidthFull()
                addThemeVariants(ButtonVariant.LUMO_ERROR)
                onLeftClick {
                    docContainer.doc?.let { docService.findByGuid(it.guid)?.let { doc -> docService.softDelete(doc) } }
                    close()
                }
            }
        }
    }
}
