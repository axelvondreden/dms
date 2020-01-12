package com.dude.dms.ui.components.search

import com.dude.dms.ui.builder.BuilderFactory
import com.github.appreciated.app.layout.component.appbar.IconButton
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.icon.VaadinIcon

class DmsSearchOverlayButton(builderFactory: BuilderFactory) : IconButton(VaadinIcon.SEARCH.create()) {

    val searchView = DmsSearchOverlayView(builderFactory)

    init {
        addClickListener { searchView.open() }
    }

    override fun onAttach(attachEvent: AttachEvent) {
        super.onAttach(attachEvent)
        attachEvent.ui.add(searchView)
    }

    override fun onDetach(detachEvent: DetachEvent) {
        super.onDetach(detachEvent)
        searchView.element.removeFromParent()
    }
}