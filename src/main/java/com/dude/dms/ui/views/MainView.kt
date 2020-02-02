package com.dude.dms.ui.views

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.mails.Mail
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.MailService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType.*
import com.dude.dms.brain.options.Options
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.search.DmsSearchOverlayButton
import com.github.appreciated.app.layout.component.appbar.AppBarBuilder
import com.github.appreciated.app.layout.component.applayout.LeftLayouts.LeftHybrid
import com.github.appreciated.app.layout.component.builder.AppLayoutBuilder
import com.github.appreciated.app.layout.component.menu.left.LeftSubmenu
import com.github.appreciated.app.layout.component.menu.left.builder.LeftAppMenuBuilder
import com.github.appreciated.app.layout.component.menu.left.items.LeftClickableItem
import com.github.appreciated.app.layout.component.menu.left.items.LeftNavigationItem
import com.github.appreciated.app.layout.component.router.AppLayoutRouterLayout
import com.github.appreciated.app.layout.entity.DefaultBadgeHolder
import com.github.appreciated.app.layout.entity.Section
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.dnd.DragSource
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.router.AfterNavigationEvent
import com.vaadin.flow.router.AfterNavigationObserver
import com.vaadin.flow.theme.lumo.Lumo
import dev.mett.vaadin.tooltip.Tooltips
import org.springframework.beans.factory.annotation.Value

@Push
class MainView(
        private val docService: DocService,
        private val mailService: MailService,
        private val tagService: TagService,
        private val attributeService: AttributeService,
        private val builderFactory: BuilderFactory,
        @param:Value("\${build.version}") private val buildVersion: String,
        eventManager: EventManager
) : AppLayoutRouterLayout<LeftHybrid>(), AfterNavigationObserver {

    private var docsBadge: DefaultBadgeHolder? = null
    private var mailsBadge: DefaultBadgeHolder? = null
    private val tagBadges = HashMap<Long, DefaultBadgeHolder>()

    init {
        init(AppLayoutBuilder.get(LeftHybrid::class.java)
                .withTitle("dms")
                .withAppBar(buildAppBar())
                .withAppMenu(buildAppMenu())
                .build())
        val ui = UI.getCurrent()

        eventManager.register(this, Doc::class, CREATE) { ui.access { docsBadge!!.increase(); fillBadgeCount(it) } }
        eventManager.register(this, Doc::class, UPDATE) { ui.access { fillBadgeCount(it) } }
        eventManager.register(this, Doc::class, DELETE) { ui.access { docsBadge!!.decrease(); fillBadgeCount(it) } }
        eventManager.register(this, Mail::class, CREATE) { ui.access { mailsBadge!!.increase(); fillBadgeCount(it) } }
        eventManager.register(this, Mail::class, UPDATE) { ui.access { fillBadgeCount(it) } }
        eventManager.register(this, Mail::class, DELETE) { ui.access { mailsBadge!!.decrease(); fillBadgeCount(it) } }
        eventManager.register(this, Attribute::class, CREATE, UPDATE, DELETE) { ui.access { appLayout.setAppMenu(buildAppMenu()) } }
        eventManager.register(this, Tag::class, CREATE, UPDATE, DELETE) { ui.access { appLayout.setAppMenu(buildAppMenu()) } }
    }

    private fun buildAppMenu(): Component {
        val docsEntry = LeftNavigationItem("Docs", VaadinIcon.FILE_TEXT.create(), DocsView::class.java)
        docsBadge = DefaultBadgeHolder(docService.count().toInt()).apply { bind(docsEntry.badge) }
        val mailsEntry = LeftNavigationItem("Mails", VaadinIcon.MAILBOX.create(), MailsView::class.java)
        mailsBadge = DefaultBadgeHolder(mailService.count().toInt()).apply { bind(mailsEntry.badge) }
        val tagsEntry = createTagsEntry()
        val attributesEntry = createAttributesEntry()
        val rulesEntry = LeftNavigationItem("Rules", VaadinIcon.MAGIC.create(), RulesView::class.java)
        return LeftAppMenuBuilder.get()
                .addToSection(Section.HEADER, LeftClickableItem("Add doc", VaadinIcon.PLUS_CIRCLE.create()) { builderFactory.docs().createDialog().build().open() })
                .add(docsEntry, mailsEntry, tagsEntry, attributesEntry, rulesEntry)
                .withStickyFooter()
                .addToSection(Section.FOOTER,
                        LeftNavigationItem("Log", VaadinIcon.CLIPBOARD_PULSE.create(), LogView::class.java),
                        LeftClickableItem(buildVersion, VaadinIcon.HAMMER.create()) { builderFactory.misc().changelog().build().open() },
                        LeftNavigationItem("Settings", VaadinIcon.COG.create(), OptionsView::class.java))
                .build()
    }

    private fun createAttributesEntry(): LeftSubmenu {
        val attributeEntries = mutableListOf<Component>(
                LeftClickableItem("Add Attribute", VaadinIcon.PLUS_CIRCLE.create()) {
                    builderFactory.attributes().createDialog().build().open()
                }
        )
        for (attribute in attributeService.findAll()) {
            val entry = LeftClickableItem(attribute.name, VaadinIcon.TEXT_LABEL.create()) { }
            val tags = tagService.findByAttribute(attribute).joinToString("\n") { it.name }
            if (tags.isNotEmpty()) {
                Tooltips.getCurrent().setTooltip(entry, "Tags:\n$tags")
            }
            attributeEntries.add(entry)
            ContextMenu().apply {
                target = entry
                isOpenOnClick = true
                addItem("Edit") { builderFactory.attributes().editDialog(attribute).build().open() }
                addItem("Delete") { builderFactory.attributes().deleteDialog(attribute).build().open() }
            }
        }
        return LeftSubmenu("Attributes", VaadinIcon.ACCESSIBILITY.create(), attributeEntries).withCloseMenuOnNavigation(false)
    }

    private fun createTagsEntry(): LeftSubmenu {
        tagBadges.clear()
        val tagEntries = mutableListOf<Component>(
                LeftClickableItem("Add Tag", VaadinIcon.PLUS_CIRCLE.create()) {
                    builderFactory.tags().createDialog().build().open()
                }
        )
        for (tag in tagService.findAll()) {
            val entry = LeftClickableItem(tag.name, VaadinIcon.TAG.create().apply { color = tag.color }) { }
            DragSource.create(entry)
            tagBadges[tag.id] = DefaultBadgeHolder().apply { bind(entry.badge) }
            fillBadgeCount(tag)
            val attrs = attributeService.findByTag(tag).joinToString("\n") { it.name }
            if (attrs.isNotEmpty()) {
                Tooltips.getCurrent().setTooltip(entry, "Attributes:\n$attrs")
            }
            tagEntries.add(entry)
            ContextMenu().apply {
                target = entry
                isOpenOnClick = true
                addItem("Docs") { UI.getCurrent().navigate<String, DocsView>(DocsView::class.java, "tag:${tag.name}") }
                addItem("Mails") { UI.getCurrent().navigate<String, MailsView>(MailsView::class.java, "tag:${tag.name}") }
                addItem("Edit") { builderFactory.tags().editDialog(tag).build().open() }
                addItem("Delete") { builderFactory.tags().deleteDialog(tag).build().open() }
            }
        }
        return LeftSubmenu("Tags", VaadinIcon.TAGS.create(), tagEntries).withCloseMenuOnNavigation(false)
    }

    private fun fillBadgeCount(doc: Doc) {
        tagService.findByDoc(doc).forEach { fillBadgeCount(it) }
    }

    private fun fillBadgeCount(mail: Mail) {
        tagService.findByMail(mail).forEach { fillBadgeCount(it) }
    }

    private fun fillBadgeCount(tag: Tag) {
        tagBadges[tag.id]?.count = docService.countByTag(tag).toInt() + mailService.countByTag(tag).toInt()
    }

    private fun buildAppBar() = AppBarBuilder.get().add(initSearchOverlayButton()).build()

    private fun initSearchOverlayButton() = DmsSearchOverlayButton(builderFactory).apply { searchView.initDataproviders(docService, tagService) }

    override fun afterNavigation(afterNavigationEvent: AfterNavigationEvent) {
        val themeList = UI.getCurrent().element.themeList
        themeList.clear()
        themeList.add(if (Options.get().view.darkMode) Lumo.DARK else Lumo.LIGHT)
    }
}