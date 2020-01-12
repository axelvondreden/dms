package com.dude.dms.ui.builder.attributes

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.tags.AttributeSelector
import kotlin.collections.HashSet

class AttributeSelectorBuilder(
        private val builderFactory: BuilderFactory,
        private val attributeService: AttributeService,
        private var selected: Set<Attribute> = HashSet()) {

    fun forTag(tag: Tag) = this.apply { selected = tag.attributes }

    fun build() = AttributeSelector(builderFactory, attributeService).also { it.selectedAttributes = selected }
}