package com.dude.dms.ui.components.cards

import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.extensions.mailFilterEditDialog
import com.github.appreciated.card.RippleClickableCard
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.label
import com.vaadin.flow.component.orderedlayout.FlexComponent

class MailFilterCard(mailFilter: MailFilter) : RippleClickableCard() {

    init {
        setWidthFull()
        addClickListener { mailFilterEditDialog(mailFilter).open() }

        horizontalLayout(isPadding = true) {
            setWidthFull()
            alignItems = FlexComponent.Alignment.CENTER

            label(mailFilter.folder)
        }
    }
}