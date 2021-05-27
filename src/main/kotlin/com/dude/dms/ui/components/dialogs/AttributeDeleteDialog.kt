package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.brain.t
import com.dude.dms.utils.attributeService
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.checkBox
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon

class AttributeDeleteDialog(private val attribute: Attribute) : DmsDialog(t("attribute.delete"), 20) {

    init {
        verticalLayout(isPadding = false, isSpacing = false) {
            setSizeFull()

            checkBox(t("attribute")) {
                value = true
                isEnabled = false
            }
            button(t("delete"), VaadinIcon.TRASH.create()) {
                setWidthFull()
                addThemeVariants(ButtonVariant.LUMO_ERROR)
                onLeftClick {
                    attributeService.delete(attribute)
                    close()
                }
            }
        }
    }
}
