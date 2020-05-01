package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.DocService
import com.dude.dms.brain.t
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon

class RuleRunnerDialog(
        private val result: Map<Doc, Set<TagContainer>>,
        private val docService: DocService
) : DmsDialog("", "60vw", "60vh") {

    private val grid = Grid<Pair<Doc, Set<TagContainer>>>().apply {
        setSelectionMode(Grid.SelectionMode.MULTI)
        setItems(result.toList())
        asMultiSelect().select(result.toList())
        addColumn { it.first.guid }.setHeader("GUID")
    }

    init {
        add(grid, Text("${result.size} ${t("docs")}"), Button(t("save"), VaadinIcon.DISC.create()) { save() }.apply { setWidthFull() })
    }

    private fun save() {
        grid.asMultiSelect().selectedItems.forEach { pair ->
            pair.first.tags = pair.first.tags.plus(pair.second.map { it.tag })
            docService.save(pair.first)
        }
        close()
    }
}