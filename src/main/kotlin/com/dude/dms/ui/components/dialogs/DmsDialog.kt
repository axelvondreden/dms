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
    CssImport(value = "./styles/dms-dialog-overlay.css", themeFor = "vaadin-dialog-overlay")
)
open class DmsDialog(title: String, initialWidth: Int? = null, initialHeight: Int? = null) : Dialog() {

    private val header = Header(
        H2(title).apply { addClassName("dialog-title") },
        Button(VaadinIcon.CLOSE_SMALL.create()) { close() }.apply { addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY) }
    ).apply { if (Options.get().view.darkMode) element.themeList.add(Lumo.LIGHT) else element.themeList.add(Lumo.DARK) }

    private val content = Div().apply { addClassName("dialog-content") }

    init {
        isDraggable = true
        isResizable = true
        width = initialWidth?.let { "${it}vw" }
        height = initialHeight?.let { "${it}vh" }
        element.setAttribute("aria-labelledby", "dialog-title")
        element.themeList.add("dms-dialog")

        super.add(header)
        super.add(content)
    }

    override fun add(vararg components: Component?) {
        content.add(*components)
    }
}
