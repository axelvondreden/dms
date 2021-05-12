package com.dude.dms.ui.components.misc

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.brain.t
import com.dude.dms.ui.components.dialogs.DocSelectDialog
import com.dude.dms.utils.aceEditor
import com.dude.dms.utils.docParser
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import de.f0rce.ace.AceEditor
import de.f0rce.ace.AceMode
import de.f0rce.ace.AceTheme

class FilterTestLayout : VerticalLayout() {

    private var docContainer: DocContainer? = null

    private lateinit var testDocLabel: Label
    private lateinit var docText: AceEditor
    private lateinit var tagGrid: Grid<TagContainer>
    private lateinit var attributeGrid: Grid<AttributeValue>

    init {
        setWidthFull()

        horizontalLayout {
            button(t("doc.select")) {
                onLeftClick {
                    DocSelectDialog {
                        docContainer = it
                        docText.value = docContainer!!.getFullText()
                        testDocLabel.text = docContainer!!.guid
                        fill(docContainer!!)
                    }.open()
                }
            }
            testDocLabel = label("")
        }
        horizontalLayout {
            alignItems = FlexComponent.Alignment.STRETCH
            setSizeFull()
            verticalLayout {
                width = "70%"
                label(t("doc.text"))
                docText = aceEditor {
                    setSizeFull()
                    theme = AceTheme.dracula
                    mode = AceMode.text
                    isReadOnly = true
                }
            }
            verticalLayout {
                setSizeFull()
                width = "30%"
                label(t("words.matched"))
                tagGrid = grid {
                    setWidthFull()
                    addColumn { it.tag }.setHeader(t("tag"))
                }
                attributeGrid = grid {
                    setWidthFull()
                    addColumn { it.attribute.name }.setHeader(t("attribute"))
                    addColumn { it.convertedValue }.setHeader(t("value"))
                }
            }
        }
    }

    fun fill(testDoc: DocContainer) {
        this.docContainer = testDoc
        val tags = docParser.discoverTags(testDoc)
        tagGrid.setItems(tags)

        val attributes = docParser.discoverAttributeValues(testDoc)
        attributeGrid.setItems(attributes)
    }

    fun refresh() {
        docContainer?.let { fill(it) }
    }

    fun clear() {
        tagGrid.setItems(emptyList())
    }
}