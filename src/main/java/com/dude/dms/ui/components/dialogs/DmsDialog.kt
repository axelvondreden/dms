package com.dude.dms.ui.components.dialogs

import com.dude.dms.brain.options.Options
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Header
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.theme.lumo.Lumo

@CssImport.Container(
        CssImport("./styles/dms-dialog.css"),
        CssImport(value="./styles/dms-dialog-overlay.css", themeFor = "vaadin-dialog-overlay")
)
open class DmsDialog(title: String, private val initialWidth: String? = null, private val initialHeight: String? = null) : Dialog() {

    private var isFullScreen = false

    private val max = Button(VaadinIcon.EXPAND_SQUARE.create()) { maximise() }.apply {
        addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY)
    }

    private val header = Header(
            H2(title).apply { addClassName("dialog-title") },
            max,
            Button(VaadinIcon.CLOSE_SMALL.create()) { close() }.apply { addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY) }
    ).apply { if (Options.get().view.darkMode) element.themeList.add(Lumo.LIGHT) else element.themeList.add(Lumo.DARK) }

    private val content = Div().apply { addClassName("dialog-content") }

    init {
        isDraggable = true
        isResizable = true

        element.themeList.add("dms-dialog")
        width = initialWidth
        height = initialHeight

        element.setAttribute("aria-labelledby", "dialog-title")

        super.add(header)
        super.add(content)
    }

    override fun add(vararg components: Component?) {
        content.add(*components)
    }

    private fun initialSize() {
        max.icon = VaadinIcon.EXPAND_SQUARE.create()
        element.themeList.remove("fullscreen")
        width = initialWidth
        height = initialHeight
    }

    private fun maximise() {
        if (isFullScreen) {
            initialSize()
        } else {
            max.icon = VaadinIcon.COMPRESS_SQUARE.create()
            element.themeList.add("fullscreen")
            setSizeFull()
        }
        isFullScreen = !isFullScreen
    }
}