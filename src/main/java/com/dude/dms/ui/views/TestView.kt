package com.dude.dms.ui.views

import com.dude.dms.ui.components.dnd.DNDContainer
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route


@Route(value = "test", layout = MainView::class)
class TestView : VerticalLayout() {

    init {
        add(DNDContainer(TextField(), Label("TEST"), Button("Test")))
    }
}