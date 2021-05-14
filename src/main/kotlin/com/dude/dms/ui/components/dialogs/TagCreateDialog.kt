package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.Tag
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.t
import com.dude.dms.ui.views.TagView
import com.dude.dms.utils.tagService
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextField

class TagCreateDialog : DmsDialog(t("tag.create"), 35) {

    private lateinit var name: TextField

    init {
        verticalLayout(isPadding = false, isSpacing = false) {
            setSizeFull()

            name = textField(t("name")) { setWidthFull() }
            horizontalLayout {
                setWidthFull()

                button(t("create"), VaadinIcon.PLUS.create()) {
                    onLeftClick { create() }
                    setWidthFull()
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                }
                button(t("close"), VaadinIcon.CLOSE.create()) {
                    onLeftClick { close() }
                    setWidthFull()
                    addThemeVariants(ButtonVariant.LUMO_ERROR)
                }
            }
        }
    }

    private fun create() {
        if (name.isEmpty) {
            LOGGER.showError(t("name.missing"), UI.getCurrent())
            return
        }
        if (tagService.findByName(name.value) != null) {
            LOGGER.showError(t("tag.exists"), UI.getCurrent())
            return
        }
        val tag = tagService.create(Tag(name.value, "white"))
        close()
        UI.getCurrent().navigate(TagView::class.java, tag.id.toString())
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(TagCreateDialog::class.java)
    }
}