package com.dude.dms.ui.builder

import com.vaadin.flow.component.Component

interface Builder<T : Component> {
    fun build(): T
}