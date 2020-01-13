package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.DocService
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.VaadinIcon

class RuleRunnerDialog(private val result: Map<Doc, Set<Tag>>, private val docService: DocService) : EventDialog<Doc>() {

    init {
        width = "60vw"
        height = "60vh"

        add(Text("${result.size} documents found"), Button("Save", VaadinIcon.DISC.create()) { save() }.apply { setWidthFull() })
    }

    private fun save() {
        result.keys.forEach { doc ->
            doc.tags.addAll(result.getValue(doc))
            docService.save(doc)
            triggerEditEvent(doc)
        }
        close()
    }
}