package com.dude.dms.ui.builder.misc

import com.dude.dms.backend.service.ChangelogService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.builder.Factory
import com.dude.dms.updater.UpdateChecker

class MiscBuilderFactory(
        builderFactory: BuilderFactory,
        private val changelogService: ChangelogService,
        private val updateChecker: UpdateChecker
) : Factory(builderFactory) {

    fun changelog() = ChangelogDialogBuilder(changelogService, updateChecker)
}