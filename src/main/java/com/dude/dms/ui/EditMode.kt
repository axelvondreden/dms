package com.dude.dms.ui

import com.vaadin.flow.component.icon.VaadinIcon

enum class EditMode(val icon: VaadinIcon, private val getter: () -> String) {

    DELETE(VaadinIcon.FILE_REMOVE, { com.dude.dms.brain.t("mode.delete") }),
    EDIT(VaadinIcon.FILE_CODE, { com.dude.dms.brain.t("mode.edit") });

    val uiName: String
        get() = getter.invoke()
}