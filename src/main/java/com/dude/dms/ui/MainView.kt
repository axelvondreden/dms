package com.dude.dms.ui

import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.parsing.PdfToDocParser
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.options.Options
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.misc.ConfirmDialog
import com.dude.dms.ui.components.search.DmsSearchOverlayButton
import com.dude.dms.ui.views.DocsView
import com.dude.dms.ui.views.LogView
import com.dude.dms.ui.views.OptionsView
import com.dude.dms.ui.views.RulesView
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
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.ButtonVariant
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
        private val tagService: TagService,
        private val attributeService: AttributeService,
        private val builderFactory: BuilderFactory,
        @param:Value("\${build.version}") private val buildVersion: String,
        pdfToDocParser: PdfToDocParser
) : AppLayoutRouterLayout<LeftHybrid>(), AfterNavigationObserver {

    private var docsBadge: DefaultBadgeHolder? = null

    init {
        init(AppLayoutBuilder.get(LeftHybrid::class.java)
                .withTitle("dms")
                .withAppBar(buildAppBar())
                .withAppMenu(buildAppMenu())
                .build())
        val ui = UI.getCurrent()
        pdfToDocParser.addEventListener("main") { success ->
            if (success) {
                ui.access {
                    docsBadge!!.increase()
                    LOGGER.showInfo("New doc added!")
                }
            }
        }
    }

    private fun buildAppMenu(): Component {
        val docsEntry = LeftNavigationItem("Docs", VaadinIcon.FILE_TEXT.create(), DocsView::class.java)
        docsBadge = DefaultBadgeHolder(docService.count().toInt()).apply { bind(docsEntry.badge) }
        val tagsEntry = createTagsEntry()
        val attributesEntry = createAttributesEntry()
        val rulesEntry = LeftNavigationItem("Rules", VaadinIcon.MAGIC.create(), RulesView::class.java)
        val logEntry = LeftNavigationItem("Log", VaadinIcon.CLIPBOARD_PULSE.create(), LogView::class.java)
        return LeftAppMenuBuilder.get()
                .addToSection(Section.HEADER, LeftClickableItem("Add doc", VaadinIcon.PLUS_CIRCLE.create()) { builderFactory.docs().createDialog().build().open() })
                .add(docsEntry, tagsEntry, attributesEntry, rulesEntry, logEntry)
                .withStickyFooter()
                .addToSection(Section.FOOTER,
                        LeftClickableItem(buildVersion, VaadinIcon.HAMMER.create()) { builderFactory.misc().changelog().build().open() },
                        LeftNavigationItem("Settings", VaadinIcon.COG.create(), OptionsView::class.java))
                .build()
    }

    private fun createAttributesEntry(): LeftSubmenu {
        val attributeEntries = mutableListOf<Component>(
                LeftClickableItem("Add Attribute", VaadinIcon.PLUS_CIRCLE.create()) {
                    builderFactory.attributes().createDialog { UI.getCurrent().page.reload() }.build().open()
                }
        )
        for (attribute in attributeService.findAll()) {
            val entry = LeftClickableItem(attribute.name, VaadinIcon.TEXT_LABEL.create()) { }
            DefaultBadgeHolder(docService.countByAttribute(attribute).toInt()).apply { bind(entry.badge) }
            val tags = attribute.tags.joinToString("\n") { it.name }
            if (tags.isNotEmpty()) {
                Tooltips.getCurrent().setTooltip(entry, "Tags:\n$tags")
            }
            attributeEntries.add(entry)
            ContextMenu().apply {
                target = entry
                addItem("Edit") { builderFactory.attributes().editDialog(attribute) { UI.getCurrent().page.reload() }.build().open() }
                addItem("Delete") {
                    ConfirmDialog("Are you sure you want to delete the item?", "Delete", VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR, ComponentEventListener {
                        attributeService.delete(attribute)
                        UI.getCurrent().page.reload()
                    }).open()
                }
            }
        }
        return LeftSubmenu("Attributes", VaadinIcon.ACCESSIBILITY.create(), attributeEntries)
    }

    private fun createTagsEntry(): LeftSubmenu {
        val tagEntries = mutableListOf<Component>(
                LeftClickableItem("Add Tag", VaadinIcon.PLUS_CIRCLE.create()) {
                    builderFactory.tags().createDialog { UI.getCurrent().page.reload() }.build().open()
                }
        )
        for (tag in tagService.findAll()) {
            val entry = LeftClickableItem(tag.name, VaadinIcon.TAG.create().apply { color = tag.color }) {
                UI.getCurrent().navigate<String, DocsView>(DocsView::class.java, "tag:${tag.name}")
            }
            DragSource.create(entry)
            DefaultBadgeHolder(docService.countByTag(tag).toInt()).apply { bind(entry.badge) }
            val attrs = tag.attributes.joinToString("\n") { it.name }
            if (attrs.isNotEmpty()) {
                Tooltips.getCurrent().setTooltip(entry, "Attributes:\n$attrs")
            }
            tagEntries.add(entry)
            ContextMenu().apply {
                target = entry
                addItem("Edit") { builderFactory.tags().editDialog(tag) { UI.getCurrent().page.reload() }.build().open() }
            }
        }
        return LeftSubmenu("Tags", VaadinIcon.TAGS.create(), tagEntries)
    }

    private fun buildAppBar() = AppBarBuilder.get().add(initSearchOverlayButton()).build()

    private fun initSearchOverlayButton() = DmsSearchOverlayButton(builderFactory).apply { searchView.initDataproviders(docService, tagService) }

    override fun afterNavigation(afterNavigationEvent: AfterNavigationEvent) {
        val themeList = UI.getCurrent().element.themeList
        themeList.clear()
        themeList.add(if (Options.get().view.darkMode) Lumo.DARK else Lumo.LIGHT)
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(MainView::class.java)
    }
}