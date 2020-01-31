package com.dude.dms.ui.builder.docs

import com.dude.dms.backend.data.docs.TextBlock
import com.dude.dms.backend.service.TextBlockService
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.components.dialogs.TextBlockEditDialog

class TextBlockEditDialogBuilder(
        private val textBlock: TextBlock,
        private val textBlockService: TextBlockService
): Builder<TextBlockEditDialog> {

    override fun build() = TextBlockEditDialog(textBlock, textBlockService)
}