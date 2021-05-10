package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.Query
import com.dude.dms.brain.t
import com.dude.dms.utils.queryService
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon

class QueryDeleteDialog(private val query: Query) : DmsDialog(t("query.delete"), 20) {

    init {
        verticalLayout(isPadding = false, isSpacing = false) {
            setSizeFull()

            button(t("delete"), VaadinIcon.TRASH.create()) {
                onLeftClick { delete() }
                setWidthFull()
                addThemeVariants(ButtonVariant.LUMO_ERROR)
            }
        }
    }

    private fun delete() {
        queryService.delete(query)
        close()
    }
}