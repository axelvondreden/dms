package com.dude.dms.ui.components.cards

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.MailService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.extensions.*
import com.dude.dms.ui.components.dialogs.DocImageDialog
import com.github.appreciated.app.layout.component.menu.left.items.LeftClickableItem
import com.github.appreciated.card.ClickableCard
import com.github.mvysny.karibudsl.v10.div
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.iconButton
import com.helger.commons.io.file.FileHelper
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.dnd.DropTarget
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.dom.Element
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import dev.mett.vaadin.tooltip.Tooltips

class DocCard(
        private val docService: DocService,
        private val tagService: TagService,
        private val mailService: MailService,
        val docContainer: DocContainer,
        private val imageDialog: DocImageDialog
) : ClickableCard() {

    private var imgDiv: Div? = null

    init {
        addClickListener { imageDialog.apply { fill(docContainer) }.open() }
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
                if (tag in docContainer.tags) {
                    docContainer.tags = docContainer.tags.plus(tag)
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
                Tooltips.getCurrent().setTooltip(this, t("edit"))
                ContextMenu(this).apply { isOpenOnClick = true }.fill()
            }
        }
        imgDiv = div {
            setSizeFull()
            style["overflow"] = "hidden"

            val img = docContainer.pages.first { it.nr == 1 }.image!!
            val image = Element("object").apply {
                setAttribute("attribute.type", "image/png")
                style["width"] = "100%"
                style["height"] = "100%"
                style["objectFit"] = "cover"
                style["objectPosition"] = "top left"
                setAttribute("data", StreamResource("image.png", InputStreamFactory { FileHelper.getInputStream(img) }))
            }
            element.appendChild(image)
        }
        div {
            addClassName("doc-info-wrapper")

            attributeValueSmallLayout {
                setWidthFull()
                fill(docContainer)
            }
            tagLayout(tagService, docContainer.tags.map { it.tag }.toMutableSet(), compact = true) { setWidthFull() }
        }

        resize()
    }

    private fun ContextMenu.fill() {
        if (docContainer.doc?.deleted == true) {
            addItem(t("view")) { imageDialog.open() }
            addItem(t("delete.forever")) { docService.delete(docContainer.doc!!) }
            addItem(t("restore")) { docService.restore(docContainer.doc!!) }
        } else {
            addItem(t("view")) { imageDialog.open() }
            addItem(t("delete")) { docDeleteDialog(docService, mailService, docContainer).open() }
        }
    }
}