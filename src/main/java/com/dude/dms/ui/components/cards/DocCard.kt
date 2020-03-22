package com.dude.dms.ui.components.cards

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.extensions.convert
import com.github.appreciated.app.layout.component.menu.left.items.LeftClickableItem
import com.github.appreciated.card.RippleClickableCard
import com.github.appreciated.card.label.SecondaryLabel
import com.helger.commons.io.file.FileHelper
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.dnd.DropTarget
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.dom.Element
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import dev.mett.vaadin.tooltip.Tooltips
import org.vaadin.olli.FileDownloadWrapper


class DocCard(
        private val builderFactory: BuilderFactory,
        private val docService: DocService,
        private val tagService: TagService,
        private val fileManager: FileManager,
        private val doc: Doc
) : RippleClickableCard() {

    private val date = SecondaryLabel(doc.documentDate?.convert())

    private val downloadButton = Button(VaadinIcon.FILE_TEXT.create()) { download() }.apply {
        Tooltips.getCurrent().setTooltip(this, "Download")
    }

    private val edit = Button(VaadinIcon.EDIT.create()) { builderFactory.docs().editDialog(doc).open() }.apply {
        Tooltips.getCurrent().setTooltip(this, t("edit"))
    }

    private var imgDiv: Div? = null

    init {
        addClickListener { builderFactory.docs().imageDialog(doc).open() }
        fill()
    }

    fun resize() {
        val size = Options.get().view.docCardSize
        width = "${size}px"
        imgDiv?.element?.style?.set("maxHeight", "${(size * 1.5).toInt()}px")
    }

    fun fill() {
        content.removeAll()

        val tagContainer = builderFactory.tags().container(tagService.findByDoc(doc).toMutableSet()).apply { setWidthFull() }
        val img = fileManager.getFirstImage(doc.guid)
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
        val titleWrapper = HorizontalLayout(date, downloadButton, edit).apply {
            setWidthFull()
            element.style["paddingRight"] = "8px"
            alignItems = FlexComponent.Alignment.CENTER
        }

        val cm = ContextMenu(this)
        cm.addItem(t("view")) { builderFactory.docs().imageDialog(doc).open() }
        cm.addItem(t("edit")) { builderFactory.docs().editDialog(doc).open() }
        cm.addItem(t("delete")) { builderFactory.docs().deleteDialog(doc).open() }
        val download = FileDownloadWrapper(StreamResource("${doc.guid}.pdf", InputStreamFactory { FileHelper.getInputStream(fileManager.getPdf(doc.guid)) }))
        val downloadButton = Button("")
        download.wrapComponent(downloadButton)
        cm.addItem(downloadButton) { downloadButton.click() }
        DropTarget.create(this).addDropListener { event ->
            val comp = event.source.ui.get().internals.activeDragSourceComponent
            if (comp is LeftClickableItem) {
                val tag = event.dragData.get() as Tag
                val tags = tagService.findByDoc(doc).toMutableSet()
                if (tags.add(tag)) {
                    doc.tags = tags
                    docService.save(doc)
                    fill()
                }
            }
        }

        add(titleWrapper, imgDiv, tagContainer)
        resize()
    }

    fun download() {
        val resource = StreamResource("${doc.guid}.pdf", InputStreamFactory { FileHelper.getInputStream(fileManager.getPdf(doc.guid)) })
        resource.setContentType("application/octet-stream")
        val a = Anchor(resource, "Download")
        UI.getCurrent().page.open(a.href)
    }
}