package com.dude.dms.ui.builder.attributes

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.tags.AttributeValueContainer

class AttributeValueContainerBuilder(private val builderFactory: BuilderFactory, private val doc: Doc) {

    fun build() = AttributeValueContainer(builderFactory, doc)
}