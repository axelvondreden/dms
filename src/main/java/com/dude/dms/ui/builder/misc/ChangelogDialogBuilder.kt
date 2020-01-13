package com.dude.dms.ui.builder.misc

import com.dude.dms.backend.service.ChangelogService
import com.dude.dms.ui.components.dialogs.ChangelogDialog
import com.dude.dms.updater.UpdateChecker

class ChangelogDialogBuilder(private val changelogService: ChangelogService, private val updateChecker: UpdateChecker) {

    fun build() = ChangelogDialog(changelogService, updateChecker)
}