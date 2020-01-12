package com.dude.dms.ui.components.search

import com.vaadin.flow.component.Component

abstract class SearchResult {

    abstract val header: String
    abstract val body: Component
    abstract fun onClick()
}