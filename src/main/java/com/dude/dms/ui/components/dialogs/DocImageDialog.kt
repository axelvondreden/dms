package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.service.DocService
import com.dude.dms.extensions.resizable
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.misc.PageSelector
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.splitlayout.SplitLayout

class DocImageDialog(builderFactory: BuilderFactory, private val docContainer: DocContainer, private val docService: DocService) : Dialog() {

    private val imageEditor = builderFactory.docs().imageEditor().apply { fill(docContainer, docContainer.pages.find { it.nr == 1 }!!) }

    private val infoLayout = builderFactory.docs().infoLayout(imageEditor).apply { fill(docContainer) }

    private val editContainer = Div(imageEditor).apply {
        maxWidth = "80vw"
        maxHeight = "80vh"
        style["overflowY"] = "auto"
    }

    private val zoomButton = Button("100%") { imageEditor.resetZoom(it.source) }

    private val pageSelector = PageSelector(docContainer.pages.size).apply {
        setChangeListener { page -> imageEditor.fill(docContainer, docContainer.pages.find { it.nr == page }!!) }
    }

    init {
        resizable()
        val shrinkButton = Button(VaadinIcon.MINUS_CIRCLE.create()) { imageEditor.shrink(zoomButton) }
        val growButton = Button(VaadinIcon.PLUS_CIRCLE.create()) { imageEditor.grow(zoomButton) }
        val horizontalLayout = HorizontalLayout(pageSelector, shrinkButton, zoomButton, growButton).apply {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        }
        val split = SplitLayout(editContainer, infoLayout).apply {
            setSizeFull()
            setSecondaryStyle("minWidth", "200px")
            setSecondaryStyle("maxWidth", "300px")
        }
        add(horizontalLayout, split)
        addOpenedChangeListener {
            if (!it.isOpened) {
                docContainer.doc?.let(docService::save)
            }
        }
    }
}