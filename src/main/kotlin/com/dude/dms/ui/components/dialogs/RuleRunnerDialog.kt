package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.brain.t
import com.dude.dms.utils.docService
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.grid
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.text
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon

class RuleRunnerDialog(private val result: Map<Doc, Set<TagContainer>>) : DmsDialog("", 60, 60) {

    init {
        val grid = grid<Pair<Doc, Set<TagContainer>>> {
            setSelectionMode(Grid.SelectionMode.MULTI)
            setItems(result.toList())
            asMultiSelect().select(result.toList())
            addColumn { it.first.guid }.setHeader("GUID")
        }
        text("${result.size} ${t("docs")}")
        button(t("save"), VaadinIcon.DISC.create()) {
            onLeftClick {
                grid.asMultiSelect().selectedItems.forEach { pair ->
                    pair.first.tags = pair.first.tags.plus(pair.second.map { it.tag })
                    docService.save(pair.first)
                }
                close()
            }
            setWidthFull()
        }
    }
}