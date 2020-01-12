package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.DocService
import com.dude.dms.ui.components.dialogs.RuleRunnerDialog

class RuleRunnerDialogBuilder(private val result: Map<Doc, Set<Tag>>, private val docService: DocService) {

    fun build() = RuleRunnerDialog(result, docService)
}