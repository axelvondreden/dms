package com.dude.dms.ui.builder.tags

import com.dude.dms.backend.brain.EditEvent
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.dialogs.TagEditDialog

class TagEditDialogBuilder(
        private val builderFactory: BuilderFactory,
        private val tag: Tag,
        private val tagService: TagService,
        private val docService: DocService,
        private var editEvent: EditEvent<Tag>? = null) {

    fun build() = TagEditDialog(builderFactory, tag, tagService, docService).also { it.editEvent = editEvent }
}