package com.dude.dms.ui.views

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.DocService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.Const
import com.dude.dms.ui.components.cards.DocCard
import com.dude.dms.utils.docCard
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route


@Route(value = Const.PAGE_RECYCLE, layout = MainView::class)
@PageTitle("Recycle Bin")
class RecycleView(private val docService: DocService, eventManager: EventManager) : VerticalLayout() {

    private var itemContainer: Div

    init {
        val ui = UI.getCurrent()
        eventManager.register(this, Doc::class, EventType.UPDATE, EventType.DELETE) { ui.access { fill() } }

        horizontalLayout {
            setWidthFull()

            button(t("recyclebin.empty"), VaadinIcon.RECYCLE.create()) {
                onLeftClick { empty() }
            }
            iconButton(VaadinIcon.MINUS_CIRCLE.create()) {
                onLeftClick { shrink() }
            }
            iconButton(VaadinIcon.PLUS_CIRCLE.create()) {
                onLeftClick { grow() }
            }
        }
        itemContainer = div {
            setSizeFull()
            style["display"] = "flex"
            style["flexWrap"] = "wrap"
        }

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
        docService.findDeleted().forEach { itemContainer.docCard(DocContainer(it)) }
    }

    private fun empty() {
        docService.findDeleted().forEach(docService::delete)
        fill()
    }
}