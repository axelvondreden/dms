package com.dude.dms.ui.builder.attributes

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.tags.AttributeSelector

class AttributeSelectorBuilder(
        private val builderFactory: BuilderFactory,
        private val attributeService: AttributeService,
        private val eventManager: EventManager,
        private var selected: Set<Attribute> = HashSet()
): Builder<AttributeSelector> {

    fun forTag(tag: Tag) = this.apply { selected = tag.attributes }

    override fun build() = AttributeSelector(builderFactory, attributeService, eventManager).also { it.selectedAttributes = selected }
}