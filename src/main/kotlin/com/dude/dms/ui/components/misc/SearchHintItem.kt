package com.dude.dms.ui.components.misc

import com.dude.dms.brain.search.hint.Hint
import com.github.mvysny.karibudsl.v10.div
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout


class SearchHintItem(val hint: Hint) : HorizontalLayout() {

    init {
        setWidthFull()
        isPadding = false
        isSpacing = false
        alignItems = FlexComponent.Alignment.END

        div {
            text = hint.text
            style["paddingLeft"] = "4px"
            style["minWidth"] = "max-content"
        }
        div {
            setWidthFull()
            text = hint.description
            style["textAlign"] = "end"
            style["paddingRight"] = "4px"
            style["color"] = "var(--lumo-secondary-text-color)"
        }
    }
}