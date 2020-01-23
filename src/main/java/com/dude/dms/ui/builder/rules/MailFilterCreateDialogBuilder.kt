package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.service.MailFilterService
import com.dude.dms.brain.CreateEvent
import com.dude.dms.brain.mail.EmailManager
import com.dude.dms.ui.components.dialogs.MailFilterCreateDialog

class MailFilterCreateDialogBuilder(
        private val mailFilterService: MailFilterService,
        private val emailManager: EmailManager,
        private var createListener: CreateEvent<MailFilter>? = null) {

    fun build() = MailFilterCreateDialog(mailFilterService, emailManager).also { it.createEvent = createListener }
}