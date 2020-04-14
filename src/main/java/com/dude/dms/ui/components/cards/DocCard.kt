package com.dude.dms.ui.components.cards

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.brain.extensions.convert
import com.github.appreciated.app.layout.component.menu.left.items.LeftClickableItem
import com.github.appreciated.card.ClickableCard
import com.github.appreciated.card.label.SecondaryLabel
import com.helger.commons.io.file.FileHelper
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.dnd.DropTarget
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.dom.Element
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import dev.mett.vaadin.tooltip.Tooltips

class DocCard(
        private val builderFactory: BuilderFactory,
        private val fileManager: FileManager,
        val docContainer: DocContainer
) : ClickableCard() {

    private var imgDiv: Div? = null

    init {
        addClickListener { builderFactory.docs().imageDialog(docContainer).open() }
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

        val tagContainer = builderFactory.tags().container(docContainer.tags.toMutableSet(), compact = true).apply { setWidthFull() }
        val attributeContainer = builderFactory.attributes().valueContainer(docContainer, true).apply { setWidthFull() }
        val img = fileManager.getFirstImage(docContainer.guid)
        val image = Element("object").apply {
            setAttribute("attribute.type", "image/png")
            style["maxWidth"] = "100%"
            setAttribute("data", StreamResource("image.png", InputStreamFactory { FileHelper.getInputStream(img) }))
        }
        imgDiv = Div().apply {
            setSizeFull()
            element.style["overflow"] = "hidden"
            element.appendChild(image)
        }

        val date = SecondaryLabel(docContainer.date?.convert())

        val menu = Button(VaadinIcon.ELLIPSIS_V.create()).apply {
            Tooltips.getCurrent().setTooltip(this, t("edit"))
        }

        val titleWrapper = HorizontalLayout(date, menu).apply {
            setWidthFull()
            element.style["paddingRight"] = "8px"
            alignItems = FlexComponent.Alignment.CENTER
        }

        ContextMenu(this).fill()
        ContextMenu(menu).apply { isOpenOnClick = true }.fill()

        DropTarget.create(this).addDropListener { event ->
            val comp = event.source.ui.get().internals.activeDragSourceComponent
            if (comp is LeftClickableItem) {
                val tag = event.dragData.get() as Tag
                if (docContainer.tags.add(tag)) {
                    fill()
                }
            }
        }

        add(titleWrapper, imgDiv, Div(tagContainer, attributeContainer).apply { addClassName("doc-info-wrapper") })
        resize()
    }

    private fun ContextMenu.fill() {
        addItem(t("view")) { builderFactory.docs().imageDialog(docContainer).open() }
        addItem(t("edit")) { builderFactory.docs().editDialog(docContainer).open() }
        addItem(t("delete")) { builderFactory.docs().deleteDialog(docContainer).open() }
    }
}