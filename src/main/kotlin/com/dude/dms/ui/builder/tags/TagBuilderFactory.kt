package com.dude.dms.ui.builder.tags

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.ui.CreateEvent
import com.dude.dms.ui.EditEvent
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.builder.Factory

class TagBuilderFactory(
        builderFactory: BuilderFactory,
        private val tagService: TagService,
        private val docService: DocService) : Factory(builderFactory) {

    fun searchResult(tag: Tag) = TagSearchResultBuilder(tag, builderFactory.tags().editDialog(tag).build(), docService)

    fun createDialog(createEvent: CreateEvent<Tag>? = null) = TagCreateDialogBuilder(builderFactory, tagService, createEvent)

    fun editDialog(tag: Tag, editEvent: EditEvent<Tag>? = null) = TagEditDialogBuilder(builderFactory, tag, tagService, docService, editEvent)

    fun selector() = TagSelectorBuilder(tagService)
}