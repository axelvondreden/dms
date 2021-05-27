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

class TagCreateDialog : DmsDialog(t("tag.create"), 35) {

    init {
        verticalLayout(isPadding = false, isSpacing = false) {
            setSizeFull()

            val name = textField(t("name")) { setWidthFull() }
            horizontalLayout {
                setWidthFull()

                button(t("create"), VaadinIcon.PLUS.create()) {
                    setWidthFull()
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                    onLeftClick {
                        when {
                            name.isEmpty -> LOGGER.showError(t("name.missing"), UI.getCurrent())
                            tagService.findByName(name.value) != null -> LOGGER.showError(t("tag.exists"), UI.getCurrent())
                            else -> {
                                val tag = tagService.create(Tag(name.value, "white"))
                                close()
                                UI.getCurrent().navigate(TagView::class.java, tag.id.toString())
                            }
                        }
                    }
                }
                button(t("close"), VaadinIcon.CLOSE.create()) {
                    setWidthFull()
                    addThemeVariants(ButtonVariant.LUMO_ERROR)
                    onLeftClick { close() }
                }
            }
        }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(TagCreateDialog::class.java)
    }
}
