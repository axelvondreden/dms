package com.dude.dms.ui.views

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.DocService
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.Const
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.cards.DocCard
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route


@Route(value = Const.PAGE_RECYCLE, layout = MainView::class)
@PageTitle("Recycle Bin")
class RecycleView(
        private val builderFactory: BuilderFactory,
        private val docService: DocService,
        private val fileManager: FileManager,
        eventManager: EventManager
) : VerticalLayout() {

    private val itemContainer = Div().apply {
        setSizeFull()
        element.style["display"] = "flex"
        element.style["flexWrap"] = "wrap"
    }

    private val empty = Button(t("recyclebin.empty"), VaadinIcon.RECYCLE.create()) { empty() }

    private val imageDialog = builderFactory.docs().imageDialog()

    init {
        val ui = UI.getCurrent()
        eventManager.register(this, Doc::class, EventType.UPDATE, EventType.DELETE) { ui.access { fill() } }

        val shrinkButton = Button(VaadinIcon.MINUS_CIRCLE.create()) { shrink() }
        val growButton = Button(VaadinIcon.PLUS_CIRCLE.create()) { grow() }

        val header = HorizontalLayout(empty, shrinkButton, growButton).apply { setWidthFull() }
        add(header, itemContainer)
        fill()
    }

    private fun grow() {
        val options = Options.get()
        if (options.view.docCardSize < 400) {
            options.view.docCardSize += 10
            options.save()
            itemContainer.children.filter { it is DocCard }.forEach { (it as DocCard).resize() }
        }
    }

    private fun shrink() {
        val options = Options.get()
        if (options.view.docCardSize > 100) {
            options.view.docCardSize -= 10
            options.save()
            itemContainer.children.filter { it is DocCard }.forEach { (it as DocCard).resize() }
        }
    }

    private fun fill() {
        itemContainer.removeAll()
        docService.findDeleted().forEach { doc ->
            val dc = DocContainer(doc)
            dc.thumbnail = fileManager.getImage(dc.guid)
            itemContainer.add(builderFactory.docs().card(dc, imageDialog))
        }
    }

    private fun empty() {
        docService.findDeleted().forEach(docService::delete)
        fill()
    }
}