package com.dude.dms.ui.components.search

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Tag

@Tag("searchResult")
abstract class SearchResult: Component() {

    abstract val header: String
    abstract val body: Component
    abstract fun onClick()
}