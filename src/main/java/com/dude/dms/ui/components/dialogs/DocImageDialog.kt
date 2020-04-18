package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.service.DocService
import com.dude.dms.ui.builder.BuilderFactory
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DocImageDialog(builderFactory: BuilderFactory, private val docContainer: DocContainer, private val docService: DocService) : Dialog() {

    private val imageEditor = builderFactory.docs().imageEditor().apply { fill(docContainer) }

    private val editContainer = Div(imageEditor).apply {
        maxWidth = "80vw"
        maxHeight = "80vh"
        style["overflowY"] = "auto"
    }

    private val zoomButton = Button("100%") { imageEditor.resetZoom(it.source) }

    init {
        val shrinkButton = Button(VaadinIcon.MINUS_CIRCLE.create()) { imageEditor.shrink(zoomButton) }
        val growButton = Button(VaadinIcon.PLUS_CIRCLE.create()) { imageEditor.grow(zoomButton) }
        val horizontalLayout = HorizontalLayout(shrinkButton, zoomButton, growButton).apply {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        }
        add(horizontalLayout, editContainer)
        addOpenedChangeListener {
            if (!it.isOpened) {
                docContainer.doc?.let(docService::save)
            }
        }
    }
}