package com.dude.dms.ui.builder.tags

import com.dude.dms.backend.brain.CreateEvent
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.TagService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.dialogs.TagCreateDialog

class TagCreateDialogBuilder(
        private val builderFactory: BuilderFactory,
        private val tagService: TagService,
        private var createEvent: CreateEvent<Tag>? = null) {

    fun build() = TagCreateDialog(builderFactory, tagService).also { it.createEvent = createEvent }
}