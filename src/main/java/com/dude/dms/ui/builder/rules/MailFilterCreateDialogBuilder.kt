package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.service.MailFilterService
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.components.dialogs.MailFilterCreateDialog

class MailFilterCreateDialogBuilder(
        private val mailFilterService: MailFilterService,
        private val mailManager: MailManager
): Builder<MailFilterCreateDialog> {

    override fun build() = MailFilterCreateDialog(mailFilterService, mailManager)
}