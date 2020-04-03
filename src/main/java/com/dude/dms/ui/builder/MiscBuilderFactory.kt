package com.dude.dms.ui.builder

import com.dude.dms.backend.service.ChangelogService
import com.dude.dms.ui.components.dialogs.ChangelogDialog
import com.dude.dms.updater.UpdateChecker

class MiscBuilderFactory(
        builderFactory: BuilderFactory,
        private val changelogService: ChangelogService,
        private val updateChecker: UpdateChecker
) : Factory(builderFactory) {

    fun changelog() = ChangelogDialog(changelogService, updateChecker)
}