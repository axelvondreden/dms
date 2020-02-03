package com.dude.dms.ui.builder.attributes

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.AttributeValueService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.tags.AttributeValueContainer

class AttributeValueContainerBuilder(
        private val builderFactory: BuilderFactory,
        private val doc: Doc,
        private val attributeValueService: AttributeValueService,
        private val eventManager: EventManager
): Builder<AttributeValueContainer> {

    override fun build() = AttributeValueContainer(builderFactory, doc, attributeValueService, eventManager)
}