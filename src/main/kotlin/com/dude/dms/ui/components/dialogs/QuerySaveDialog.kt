package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.Query
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.t
import com.dude.dms.utils.queryService
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextField

class QuerySaveDialog(private val searchText: String) : DmsDialog(t("search.save"), 40) {

    private var name: TextField

    init {
        name = textField("Name") {
            setWidthFull()
        }
        horizontalLayout {
            setWidthFull()

            button(t("save"), VaadinIcon.DISC.create()) {
                onLeftClick { save() }
                setWidthFull()
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            }
            button(t("close"), VaadinIcon.CLOSE.create()) {
                onLeftClick { close() }
                setWidthFull()
                addThemeVariants(ButtonVariant.LUMO_ERROR)
            }
        }

        name.focus()
    }

    private fun save() {
        if (name.isEmpty) {
            LOGGER.showError(t("text.missing"), UI.getCurrent())
            return
        }
        if (queryService.findByName(name.value) != null) {
            LOGGER.showError(t("query.exists"), UI.getCurrent())
            return
        }
        queryService.create(Query(name.value, searchText))
        close()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(QuerySaveDialog::class.java)
    }
}