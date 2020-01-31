package com.dude.dms.ui.builder.tags

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.builder.Factory

class TagBuilderFactory(
        builderFactory: BuilderFactory,
        private val tagService: TagService,
        private val docService: DocService) : Factory(builderFactory) {

    fun searchResult(tag: Tag) = TagSearchResultBuilder(tag, builderFactory.tags().editDialog(tag).build(), docService)

    fun createDialog() = TagCreateDialogBuilder(builderFactory, tagService)

    fun editDialog(tag: Tag) = TagEditDialogBuilder(builderFactory, tag, tagService, docService)

    fun selector() = TagSelectorBuilder(tagService)
}