package com.dude.dms.ui.components.cards

import com.dude.dms.backend.data.mails.MailFilter
import com.github.appreciated.card.RippleClickableCard
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.label
import com.vaadin.flow.component.orderedlayout.FlexComponent

class MailFilterCard(mailFilter: MailFilter) : RippleClickableCard() {

    init {
        setWidthFull()
        builderFactory.rules().mailEditDialog(mailFilter).open()

        horizontalLayout(isPadding = true) {
            setWidthFull()
            minHeight = "10vh"
            alignItems = FlexComponent.Alignment.CENTER

            label(mailFilter.folder)
        }
    }
}