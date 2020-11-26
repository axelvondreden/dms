package com.dude.dms.ui.components.cards

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.utils.*
import com.dude.dms.ui.components.dialogs.DocImageDialog
import com.github.appreciated.app.layout.component.menu.left.items.LeftClickableItem
import com.github.appreciated.card.ClickableCard
import com.github.mvysny.karibudsl.v10.div
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.iconButton
import com.helger.commons.io.file.FileHelper
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.dnd.DropTarget
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.dom.Element
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import com.vaadin.flow.server.VaadinSession
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DocCard(val docContainer: DocContainer) : ClickableCard() {

    private var imgDiv: Div? = null

    private val viewUI = UI.getCurrent()

    init {
        addClickListener { DocImageDialog(docContainer).open() }
        fill()
        addClassName("doc-card")
    }

    fun resize() {
        val size = Options.get().view.docCardSize
        width = "${size}px"
        imgDiv?.element?.style?.set("height", "${(size * 1.2).toInt()}px")
    }

    fun fill() {
        content.removeAll()

        ContextMenu(this).fill()

        DropTarget.create(this).addDropListener { event ->
            val comp = event.source.ui.get().internals.activeDragSourceComponent
            if (comp is LeftClickableItem) {
                val tag = TagContainer(event.dragData.get() as Tag)
                if (tag !in docContainer.tags) {
                    docContainer.tags = docContainer.tags.plus(tag)
                    docContainer.doc?.let(docService::save)
                    fill()
                }
            }
        }

        horizontalLayout {
            setWidthFull()
            style["paddingRight"] = "8px"
            alignItems = FlexComponent.Alignment.CENTER

            secondaryLabel(docContainer.date?.convert())
            iconButton(VaadinIcon.ELLIPSIS_V.create()) {
                tooltip(t("edit"))
                ContextMenu(this).apply { isOpenOnClick = true }.fill()
            }
        }
        imgDiv = div {
            setSizeFull()
            style["overflow"] = "hidden"

            val image = Element("object").apply {
                setAttribute("attribute.type", "image/png")
                style["width"] = "100%"
                style["height"] = "100%"
                style["objectFit"] = "cover"
                style["objectPosition"] = "top left"
            }
            element.appendChild(image)

            GlobalScope.launch {
                docContainer.thumbnail = fileManager.getImage(docContainer.guid)
                viewUI.access {
                    image.setAttribute("data", StreamResource("image.png", InputStreamFactory {
                        FileHelper.getInputStream(docContainer.thumbnail)
                    }))
                }
            }
        }
        div {
            addClassName("doc-info-wrapper")

            attributeValueSmallLayout {
                setWidthFull()
                fill(docContainer)
            }
            tagLayout(docContainer.tags.map { it.tag }.toMutableSet(), compact = true) { setWidthFull() }
        }

        resize()
    }

    private fun ContextMenu.fill() {
        if (docContainer.doc?.deleted == true) {
            addItem(t("delete.forever")) { docService.delete(docContainer.doc!!) }
            addItem(t("restore")) { docService.restore(docContainer.doc!!) }
        } else {
            addItem("Download") {
                val resource = StreamResource("${docContainer.guid}.pdf") { -> fileManager.getPdf(docContainer.guid).inputStream() }
                val registration = VaadinSession.getCurrent().resourceRegistry.registerResource(resource)
                UI.getCurrent().page.setLocation(registration.resourceUri)
            }
            addItem(t("delete")) { docDeleteDialog(docContainer).open() }
        }
    }
}