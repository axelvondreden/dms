package com.dude.dms.ui

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.icon.VaadinIcon

enum class EditMode(val icon: VaadinIcon, private val getter: () -> String, val shortcut: Key) {

    DELETE(VaadinIcon.FILE_REMOVE, { com.dude.dms.brain.t("mode.delete") + " (D)" }, Key.KEY_D),
    EDIT(VaadinIcon.FILE_CODE, { com.dude.dms.brain.t("mode.edit") + " (E)" }, Key.KEY_E);

    val uiName: String
        get() = getter.invoke()
}