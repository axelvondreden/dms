package com.dude.dms.ui.components.misc

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.extensions.attributeValueLayout
import com.dude.dms.extensions.tagSelector
import com.dude.dms.ui.components.tags.AttributeValueLayout
import com.dude.dms.ui.components.tags.TagSelector
import com.vaadin.flow.component.html.Div

class DocInfoLayout(imageEditor: DocImageEditor) : Div() {

    private var docContainer: DocContainer? = null

    private lateinit var attributeValueLayout: AttributeValueLayout

    private var tagSelector: TagSelector

    init {
        setSizeFull()

        tagSelector = tagSelector {
            maxHeight = "50%"
            asMultiSelect().addSelectionListener { event ->
                docContainer?.tags = event.value
                docContainer?.let { attributeValueLayout.fill(it) }
            }
        }
        attributeValueLayout = attributeValueLayout(imageEditor) {
            setWidthFull()
            maxHeight = "50%"
        }
    }

    fun validate() = attributeValueLayout.validate()

    fun fill(docContainer: DocContainer) {
        this.docContainer = docContainer
        tagSelector.selectedTags = docContainer.tags
        attributeValueLayout.fill(docContainer)
    }

    fun clear() {
        tagSelector.selectedTags = emptySet()
        attributeValueLayout.clear()
    }
}