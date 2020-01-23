package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.service.MailFilterService
import com.dude.dms.brain.DeleteEvent
import com.dude.dms.brain.EditEvent
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.cards.MailFilterCard

class MailFilterCardBuilder(
        private val builderFactory: BuilderFactory,
        private val mailFilter: MailFilter,
        private val mailFilterService: MailFilterService,
        private var editEvent: EditEvent<MailFilter>? = null,
        private var deleteEvent: DeleteEvent<MailFilter>? = null) {

    fun build() = MailFilterCard(mailFilter).also {
        it.addClickListener {
            builderFactory.rules().mailEditDialog(mailFilter, editEvent, deleteEvent).build().open()
        }
    }
}