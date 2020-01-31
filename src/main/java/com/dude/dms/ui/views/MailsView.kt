package com.dude.dms.ui.views

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.mails.Mail
import com.dude.dms.backend.service.MailService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
import com.dude.dms.ui.Const
import com.dude.dms.ui.components.tags.TagContainer
import com.dude.dms.ui.extensions.convert
import com.github.appreciated.app.layout.component.menu.left.items.LeftClickableItem
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.dnd.GridDropMode
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import dev.mett.vaadin.tooltip.Tooltips

@Route(value = Const.PAGE_MAILS, layout = MainView::class)
@PageTitle("Mails")
class MailsView(
        private val mailService: MailService,
        private val tagService: TagService,
        eventManager: EventManager
) : GridView<Mail>() {

    init {
        val ui = UI.getCurrent()

        eventManager.register(this::class, Mail::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { ui.access { fillGrid() } }
        eventManager.register(this::class, Tag::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { ui.access { fillGrid() } }

        grid.addColumn { it.received.convert() }.setHeader("Date")
        grid.addColumn { it.sender }.setHeader("From")
        grid.addColumn { it.subject }.setHeader("Subject")
        grid.addComponentColumn { TagContainer(it.tags) }.setHeader("Tags")
        grid.addComponentColumn { createGridActions(it) }
        grid.columns.forEach { it.setResizable(true).setAutoWidth(true) }
        grid.isColumnReorderingAllowed = true

        grid.dropMode = GridDropMode.ON_TOP
        grid.addDropListener { event ->
            // Workaround
            val comp = event.source.ui.get().internals.activeDragSourceComponent
            if (comp is LeftClickableItem) {
                val mail = event.dropTargetItem.get()
                tagService.findByName(comp.name)?.let { tag ->
                    val tags = tagService.findByMail(mail).toMutableSet()
                    if (tags.add(tag)) {
                        mail.tags = tags
                        mailService.save(mail)
                        grid.dataProvider.refreshAll()
                    }
                }
            }
        }

        fillGrid()
    }

    private fun createGridActions(mail: Mail): HorizontalLayout {
        val docs = Button(VaadinIcon.FILE_TEXT.create()) { UI.getCurrent().navigate(DocsView::class.java, "mail:${mail.id}") }
        Tooltips.getCurrent().setTooltip(docs, "Docs")
        return HorizontalLayout(docs)
    }

    private fun fillGrid() {
        grid.setItems(mailService.findAll())
    }
}