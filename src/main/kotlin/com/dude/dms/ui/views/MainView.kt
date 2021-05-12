package com.dude.dms.ui.views

import com.dude.dms.backend.data.filter.DocFilter
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.DocFilterService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType.*
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.polling.DocImportService
import com.dude.dms.brain.t
import com.dude.dms.utils.tooltip
import com.dude.dms.ui.components.dialogs.*
import com.github.appreciated.app.layout.component.applayout.LeftLayouts
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
import com.vaadin.flow.component.UIDetachedException
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.dnd.DragSource
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.router.AfterNavigationEvent
import com.vaadin.flow.router.AfterNavigationObserver
import com.vaadin.flow.server.InitialPageSettings
import com.vaadin.flow.server.PageConfigurator
import com.vaadin.flow.theme.lumo.Lumo
import org.springframework.beans.factory.annotation.Value
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.schedule

@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
@CssImport("./styles/styles.css")
@Push
class MainView(
    private val docService: DocService,
    private val tagService: TagService,
    private val attributeService: AttributeService,
    private val docImportService: DocImportService,
    private val docFilterService: DocFilterService,
    @param:Value("\${build.version}") private val buildVersion: String,
    eventManager: EventManager
) : AppLayoutRouterLayout<LeftLayouts.LeftResponsiveHybridNoAppBar>(), AfterNavigationObserver, PageConfigurator {

    private var docsBadge: DefaultBadgeHolder? = null
    private var recycleBadge: DefaultBadgeHolder? = null
    private var importsBadge: DefaultBadgeHolder? = null
    private val tagBadges = HashMap<Long, DefaultBadgeHolder>()

    init {
        init(AppLayoutBuilder.get(LeftLayouts.LeftResponsiveHybridNoAppBar::class.java)
                .withTitle("dms")
                .withAppMenu(buildAppMenu())
                .build())
        val ui = UI.getCurrent()

        eventManager.register(this, Doc::class, CREATE) { ui.access { docsBadge!!.increase(); fillBadgeCount(it) } }
        eventManager.register(this, Doc::class, UPDATE) { ui.access { fillBadgeCount(it) } }
        eventManager.register(this, Doc::class, DELETE) { ui.access { docsBadge!!.decrease(); fillBadgeCount(it) } }
        eventManager.register(this, Attribute::class, CREATE, UPDATE, DELETE) { ui.access { appLayout.setAppMenu(buildAppMenu()) } }
        eventManager.register(this, Tag::class, CREATE, UPDATE, DELETE) { ui.access { appLayout.setAppMenu(buildAppMenu()) } }
        eventManager.register(this, DocFilter::class, CREATE, UPDATE, DELETE) { ui.access { appLayout.setAppMenu(buildAppMenu()) } }

        Timer().schedule(10 * 1000, 10 * 1000) {
            try {
                ui.access { importsBadge!!.count = docImportService.count }
            } catch (e: UIDetachedException) { }
        }
    }

    private fun buildAppMenu(): Component {
        val importDocEntry = LeftNavigationItem(t("import"), VaadinIcon.PLUS_CIRCLE.create(), DocImportView::class.java)
        importsBadge = DefaultBadgeHolder(docImportService.count).apply { bind(importDocEntry.badge) }
        val docsEntry = LeftNavigationItem(t("docs"), VaadinIcon.FILE_TEXT.create(), DocsView::class.java)
        docsBadge = DefaultBadgeHolder(docService.count().toInt()).apply { bind(docsEntry.badge) }
        val recycleEntry = LeftNavigationItem(t("recyclebin"), VaadinIcon.TRASH.create(), RecycleView::class.java)
        recycleBadge = DefaultBadgeHolder(docService.countDeleted().toInt()).apply { bind(recycleEntry.badge) }
        val tagsEntry = createTagsEntry()
        val attributesEntry = createAttributesEntry()
        val queriesEntry = createQueriesEntry()
        return LeftAppMenuBuilder.get()
                .add(importDocEntry, docsEntry, tagsEntry, attributesEntry, queriesEntry)
                .withStickyFooter()
                .addToSection(Section.FOOTER,
                        LeftNavigationItem("Log", VaadinIcon.CLIPBOARD_PULSE.create(), LogView::class.java),
                        LeftClickableItem(buildVersion, VaadinIcon.HAMMER.create()) { ChangelogDialog().open() },
                        LeftNavigationItem(t("administration"), VaadinIcon.DASHBOARD.create(), AdminView::class.java),
                        LeftNavigationItem(t("settings"), VaadinIcon.COG.create(), OptionsView::class.java),
                        recycleEntry)
                .build()
    }

    private fun createAttributesEntry(): LeftSubmenu {
        val attributeEntries = mutableListOf<Component>(
                LeftClickableItem(t("attribute.new"), VaadinIcon.PLUS_CIRCLE.create()) {
                    AttributeCreateDialog().open()
                }
        )
        for (attribute in attributeService.findAll()) {
            val icon = when (attribute.type) {
                Attribute.Type.STRING -> VaadinIcon.TEXT_LABEL
                Attribute.Type.INT -> VaadinIcon.HASH
                Attribute.Type.FLOAT -> VaadinIcon.SUPERSCRIPT
                Attribute.Type.DATE -> VaadinIcon.CALENDAR
            }
            val entry = LeftClickableItem(attribute.name, icon.create()) { }
            val tags = tagService.findByAttribute(attribute).joinToString("\n") { it.name }
            if (tags.isNotEmpty()) {
                entry.tooltip("${t("tags")}:\n$tags")
            }
            attributeEntries.add(entry)
            ContextMenu().apply {
                target = entry
                isOpenOnClick = true
                addItem(t("edit")) { UI.getCurrent().navigate(AttributeView::class.java, attribute.id.toString()) }
                addItem(t("delete")) { AttributeDeleteDialog(attribute).open() }
            }
        }
        return LeftSubmenu(t("attributes"), VaadinIcon.FILE_TREE.create(), attributeEntries).withCloseMenuOnNavigation(false)
    }

    private fun createTagsEntry(): LeftSubmenu {
        tagBadges.clear()
        val tagEntries = mutableListOf<Component>(
                LeftClickableItem(t("tag.add"), VaadinIcon.PLUS_CIRCLE.create()) {
                    TagCreateDialog().open()
                }
        )
        for (tag in tagService.findAll()) {
            val entry = LeftClickableItem(tag.name, VaadinIcon.TAG.create().apply { color = tag.color }) { }
            DragSource.create(entry).addDragStartListener { it.setDragData(tag) }
            tagBadges[tag.id] = DefaultBadgeHolder().apply { bind(entry.badge) }
            fillBadgeCount(tag)
            val attrs = tag.attributes.joinToString("\n") { it.name }
            if (attrs.isNotEmpty()) {
                entry.tooltip("${t("attributes")}:\n$attrs")
            }
            tagEntries.add(entry)
            ContextMenu().apply {
                target = entry
                isOpenOnClick = true
                addItem(t("docs")) { UI.getCurrent().navigate<String, DocsView>(DocsView::class.java, "tag:${tag.name}") }
                addItem(t("edit")) { UI.getCurrent().navigate(TagView::class.java, tag.id.toString()) }
                addItem(t("delete")) { TagDeleteDialog(tag).open() }
            }
        }
        return LeftSubmenu(t("tags"), VaadinIcon.TAGS.create(), tagEntries).withCloseMenuOnNavigation(false)
    }

    private fun createQueriesEntry() = LeftSubmenu(
        t("my.queries"),
        VaadinIcon.FOLDER_SEARCH.create(),
        docFilterService.findAll().map { query ->
            LeftClickableItem(query.name, VaadinIcon.SEARCH.create()) { }.also { item ->
                ContextMenu().apply {
                    target = item
                    isOpenOnClick = true
                    addItem(t("search")) { UI.getCurrent().navigate<String, DocsView>(DocsView::class.java, "query:${query.id}") }
                    addItem(t("delete")) { QueryDeleteDialog(query).open() }
                }
            }
        }
    ).withCloseMenuOnNavigation(false)

    private fun fillBadgeCount(doc: Doc) {
        doc.tags.forEach { fillBadgeCount(it) }
        recycleBadge?.count = docService.countDeleted().toInt()
    }

    private fun fillBadgeCount(tag: Tag) {
        tagBadges[tag.id]?.count = docService.countByTag(tag).toInt()
    }

    override fun afterNavigation(afterNavigationEvent: AfterNavigationEvent) {
        val themeList = UI.getCurrent().element.themeList
        themeList.clear()
        themeList.add(if (Options.get().view.darkMode) Lumo.DARK else Lumo.LIGHT)
    }

    override fun configurePage(settings: InitialPageSettings) {
        settings.loadingIndicatorConfiguration.isApplyDefaultTheme = false
    }
}