package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.service.ChangelogService
import com.dude.dms.brain.t
import com.dude.dms.updater.UpdateChecker
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.textArea
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon

class ChangelogDialog(changelogService: ChangelogService, updateChecker: UpdateChecker) : DmsDialog(t("changelog"), 70, 70) {

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