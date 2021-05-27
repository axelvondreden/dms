package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.Tag
import com.dude.dms.brain.t
import com.dude.dms.utils.attributeService
import com.dude.dms.utils.tagService
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.checkBox
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon

class TagDeleteDialog(private val tag: Tag) : DmsDialog(t("tag.delete"), 20) {

    init {
        verticalLayout(isPadding = false, isSpacing = false) {
            setSizeFull()

            checkBox(t("tag")) {
                isEnabled = false
                value = true
            }
            val attributeCheck = checkBox("${t("attributes")} (${tag.attributes.size})")
            button(t("delete"), VaadinIcon.TRASH.create()) {
                setWidthFull()
                addThemeVariants(ButtonVariant.LUMO_ERROR)
                onLeftClick {
                    if (attributeCheck.value) {
                        tag.attributes.forEach(attributeService::delete)
                        tag.attributes = emptySet()
                    }
                    tagService.delete(tagService.save(tag))
                    close()
                }
            }
        }
    }
}
