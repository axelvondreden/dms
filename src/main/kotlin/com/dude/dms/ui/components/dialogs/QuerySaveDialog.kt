package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.filter.DocFilter
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

class QuerySaveDialog(private val searchText: String) : DmsDialog(t("search.save"), 40) {

    init {
        val name = textField("Name") { setWidthFull() }
        horizontalLayout {
            setWidthFull()

            button(t("save"), VaadinIcon.DISC.create()) {
                setWidthFull()
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                onLeftClick {
                    when {
                        name.isEmpty -> LOGGER.showError(t("text.missing"), UI.getCurrent())
                        queryService.findByName(name.value) != null -> LOGGER.showError(t("query.exists"), UI.getCurrent())
                        else -> {
                            queryService.create(DocFilter(name.value, searchText))
                            close()
                        }
                    }
                }
            }
            button(t("close"), VaadinIcon.CLOSE.create()) {
                setWidthFull()
                addThemeVariants(ButtonVariant.LUMO_ERROR)
                onLeftClick { close() }
            }
        }

        name.focus()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(QuerySaveDialog::class.java)
    }
}
