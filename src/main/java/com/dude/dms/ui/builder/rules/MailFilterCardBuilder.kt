package com.dude.dms.ui.builder.rules

import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.ui.builder.Builder
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.cards.MailFilterCard

class MailFilterCardBuilder(
        private val builderFactory: BuilderFactory,
        private val mailFilter: MailFilter
): Builder<MailFilterCard> {

    override fun build() = MailFilterCard(mailFilter).also {
        it.addClickListener {
            builderFactory.rules().mailEditDialog(mailFilter).build().open()
        }
    }
}