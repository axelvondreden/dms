package com.dude.dms.ui.components.dialogs

import com.dude.dms.brain.t
import com.dude.dms.extensions.changelogService
import com.dude.dms.extensions.updateChecker
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.textArea
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon

class ChangelogDialog : DmsDialog(t("changelog"), 70, 70) {

    init {
        button(t("check"), VaadinIcon.REFRESH.create()) {
            onLeftClick { updateChecker.check(false) }
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }

        changelogService.findAll().sortedBy { it.published }.reversed().forEach { changelog ->
            textArea(changelog.version) {
                setWidthFull()
                value = changelog.body
                isReadOnly = true
            }
        }
    }
}