package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.service.MailFilterService
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.components.dialogs.MailFilterEditDialog

class MailFilterEditDialogBuilder(
        private val mailFilter: MailFilter,
        private val mailFilterService: MailFilterService,
        private val mailManager: MailManager
): Builder<MailFilterEditDialog> {

    override fun build() = MailFilterEditDialog(mailFilter, mailFilterService, mailManager)
}