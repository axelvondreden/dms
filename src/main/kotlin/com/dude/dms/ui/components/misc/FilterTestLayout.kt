package com.dude.dms.ui.components.misc

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.filter.AttributeFilter
import com.dude.dms.backend.data.filter.TagFilter
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.components.dialogs.DocSelectDialog
import com.dude.dms.ui.components.tags.TagLabel
import com.dude.dms.utils.aceEditor
import com.dude.dms.utils.docParser
import com.dude.dms.utils.tagFilterService
import com.github.mvysny.karibudsl.v10.*
import com.hilerio.ace.AceEditor
import com.hilerio.ace.AceMode
import com.hilerio.ace.AceTheme
import com.vaadin.flow.component.ClientCallable
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout


class FilterTestLayout : VerticalLayout() {

    private var extended = false
    private val extendedLayout: HorizontalLayout

    private lateinit var toggleButton: Button
    private lateinit var docSelectButton: Button

    private lateinit var testText: AceEditor
    private lateinit var tagGrid: Grid<Pair<TagContainer, Int>>
    private lateinit var attributeGrid: Grid<Pair<AttributeValue, Int>>

    private var tempTagFilter: TagFilter? = null
    private var tempAttributeFilter: AttributeFilter? = null

    init {
        element.executeJs("window.testLayout = this;")
        isPadding = false
        setWidthFull()
        maxHeight = "35em"
        style["borderTop"] = "1px solid var(--lumo-contrast-10pct)"

        horizontalLayout(isPadding = false) {
            toggleButton = button("Test", VaadinIcon.PLAY.create()) {
                onLeftClick { testToggle() }
                addThemeVariants(ButtonVariant.LUMO_SUCCESS)
            }
            docSelectButton = button(t("doc.select")) {
                isVisible = false
                onLeftClick { DocSelectDialog { testText.value = it.getFullText() }.open() }
            }
        }

        extendedLayout = horizontalLayout {
            isVisible = false
            alignItems = FlexComponent.Alignment.STRETCH
            setSizeFull()
            verticalLayout(isPadding = false) {
                width = "40%"

                testText = aceEditor {
                    setSizeFull()
                    theme = if (Options.get().view.darkMode) AceTheme.dracula else AceTheme.clouds
                    mode = AceMode.text
                    element.executeJs("var ed = this.editor; ed.on('change', function(e) { window.testLayout.\$server.refresh(ed.getValue()); })")
                }
            }
            tagGrid = grid {
                width = "30%"
                setSelectionMode(Grid.SelectionMode.SINGLE)
                addComponentColumn { TagLabel(it.first.tag) }.setHeader(t("tag"))
                addSelectionListener { event ->
                    event.firstSelectedItem.ifPresent {
                        testText.setSelection(it.second, 0, it.second, 200, true)
                    }
                }
            }
            attributeGrid = grid {
                width = "30%"
                setSelectionMode(Grid.SelectionMode.SINGLE)
                addColumn { it.first.attribute.name }.setHeader(t("attribute"))
                addColumn { it.first.convertedValue }.setHeader(t("value"))
                addSelectionListener { event ->
                    event.firstSelectedItem.ifPresent {
                        testText.setSelection(it.second, 0, it.second, 200, true)
                    }
                }
            }
        }
    }

    private fun testToggle() {
        extended = !extended
        extendedLayout.isVisible = extended
        docSelectButton.isVisible = extended
        if (extended) {
            toggleButton.removeThemeVariants(ButtonVariant.LUMO_SUCCESS)
            toggleButton.addThemeVariants(ButtonVariant.LUMO_ERROR)
        } else {
            toggleButton.removeThemeVariants(ButtonVariant.LUMO_ERROR)
            toggleButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS)
        }
    }

    @ClientCallable
    fun refresh(text: String) {
        testText.value = text
        fill(tempTagFilter, tempAttributeFilter)
    }

    fun fill(overrideTagFilter: TagFilter? = null, overrideAttributeFilter: AttributeFilter? = null) {
        tempTagFilter = overrideTagFilter
        tempAttributeFilter = overrideAttributeFilter

        val doc = DocContainer.fromString(testText.value)
        val tagFilters = if (overrideTagFilter == null) {
            tagFilterService.findAll()
        } else {
            tagFilterService.findAll().filter { it.tag != overrideTagFilter.tag }.plus(overrideTagFilter)
        }
        val tags = docParser.filterTags(doc, tagFilters)
        tagGrid.setItems(tags.toList())

        val attributeFilters = if (overrideAttributeFilter == null) {
            tags.flatMap { it.key.tag.attributes }.mapNotNull { it.attributeFilter }
        } else {
            tags.flatMap { it.key.tag.attributes }
                .mapNotNull { it.attributeFilter }
                .filter { it.attribute != overrideAttributeFilter.attribute }
                .plus(overrideAttributeFilter)
        }
        val attributeValues = docParser.discoverAttributeValues(doc, attributeFilters)
        attributeGrid.setItems(attributeValues.toList())
    }

    fun clear() {
        tagGrid.setItems(emptyList())
        attributeGrid.setItems(emptyList())
    }
}