package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.service.ChangelogService
import com.dude.dms.updater.UpdateChecker
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextArea

class ChangelogDialog(changelogService: ChangelogService, updateChecker: UpdateChecker) : Dialog() {

    init {
        width = "70vw"
        height = "70vh"

        add(Button("Check for updates", VaadinIcon.REFRESH.create()) { updateChecker.check(false) }.apply { addThemeVariants(ButtonVariant.LUMO_PRIMARY) })

        changelogService.findAll().sortedBy { it.published }.forEach { changelog ->
            val area = TextArea(changelog.version, changelog.body, "").apply {
                isReadOnly = true
                setWidthFull()
            }
            add(area)
        }
    }
}