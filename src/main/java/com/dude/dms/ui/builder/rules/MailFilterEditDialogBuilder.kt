package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.service.MailFilterService
import com.dude.dms.brain.DeleteEvent
import com.dude.dms.brain.EditEvent
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.ui.components.dialogs.MailFilterEditDialog

class MailFilterEditDialogBuilder(
        private val mailFilter: MailFilter,
        private val mailFilterService: MailFilterService,
        private val mailManager: MailManager,
        private var editEvent: EditEvent<MailFilter>? = null,
        private var deleteEvent: DeleteEvent<MailFilter>? = null) {

    fun build() = MailFilterEditDialog(mailFilter, mailFilterService, mailManager).also {
        it.editEvent = editEvent
        it.deleteEvent = deleteEvent
    }
}