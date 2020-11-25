package com.dude.dms.brain.search.hint

import com.vaadin.flow.component.icon.VaadinIcon

data class Hint(
        val text: String,
        val description: String? = null,
        val caretBackwardsMovement: Int = 0,
        val icon: VaadinIcon? = null
)