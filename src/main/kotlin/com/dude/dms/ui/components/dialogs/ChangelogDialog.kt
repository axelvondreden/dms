package com.dude.dms.ui.components.dialogs

import com.dude.dms.brain.t
import com.dude.dms.extensions.changelogService
import com.github.mvysny.karibudsl.v10.label
import com.github.mvysny.karibudsl.v10.textArea

class ChangelogDialog : DmsDialog(t("changelog"), 70, 70) {

    init {
        label(changelogService.currentVersion)

        changelogService.findAll().sortedBy { it.published }.reversed().forEach { changelog ->
            textArea(changelog.tag) {
                setWidthFull()
                value = changelog.body
                isReadOnly = true
            }
        }
    }
}
