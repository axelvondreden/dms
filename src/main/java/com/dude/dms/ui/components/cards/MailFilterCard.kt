package com.dude.dms.ui.components.cards

import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.service.MailFilterService
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.ui.mailFilterEditDialog
import com.github.appreciated.card.RippleClickableCard
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.label
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.orderedlayout.FlexComponent

class MailFilterCard(mailFilterService: MailFilterService, mailFilter: MailFilter, mailManager: MailManager) : RippleClickableCard() {

    init {
        setWidthFull()
        onLeftClick { mailFilterEditDialog(mailFilterService, mailFilter, mailManager).open() }

        horizontalLayout(isPadding = true) {
            setWidthFull()
            minHeight = "10vh"
            alignItems = FlexComponent.Alignment.CENTER

            label(mailFilter.folder)
        }
    }
}