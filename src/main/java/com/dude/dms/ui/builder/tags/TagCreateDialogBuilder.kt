package com.dude.dms.ui.builder.tags

import com.dude.dms.backend.service.TagService
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.dialogs.TagCreateDialog

class TagCreateDialogBuilder(
        private val builderFactory: BuilderFactory,
        private val tagService: TagService
): Builder<TagCreateDialog> {

    override fun build() = TagCreateDialog(builderFactory, tagService)
}