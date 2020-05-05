package com.dude.dms.ui.components.misc

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.ui.builder.BuilderFactory
import com.vaadin.flow.component.html.Div

class DocInfoLayout(builderFactory: BuilderFactory, imageEditor: DocImageEditor) : Div() {

    private var docContainer: DocContainer? = null

    private val attributeValueContainer = builderFactory.attributes().valueContainer(imageEditor).apply {
        setWidthFull()
        maxHeight = "50%"
    }

    private val tagSelector = builderFactory.tags().selector().apply {
        maxHeight = "50%"
        asMultiSelect().addSelectionListener { event ->
            docContainer?.tags = event.value
            docContainer?.let { attributeValueContainer.fill(it) }
        }
    }

    init {
        setSizeFull()
        add(tagSelector, attributeValueContainer)
    }

    fun validate() = attributeValueContainer.validate()

    fun fill(docContainer: DocContainer) {
        this.docContainer = docContainer
        tagSelector.selectedTags = docContainer.tags
        attributeValueContainer.fill(docContainer)
    }

    fun clear() {
        tagSelector.selectedTags = emptySet()
        attributeValueContainer.clear()
    }
}